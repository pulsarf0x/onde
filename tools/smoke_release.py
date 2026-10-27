#!/usr/bin/env python3
"""Release UI smoke test on a dedicated emulator; never sends audio or touches a phone."""
import argparse
import json
import hashlib
import os
from pathlib import Path
import re
import shutil
import subprocess
import time
import xml.etree.ElementTree as ET

parser = argparse.ArgumentParser()
parser.add_argument('--serial', required=True)
parser.add_argument('--apk', type=Path, default=Path('app/build/outputs/apk/release/app-release.apk'))
parser.add_argument('--offline-screenshots', action='store_true', help='Disable emulator Wi-Fi while capturing to avoid exposing real LAN device names')
parser.add_argument('--output', type=Path, default=Path('work/emulator-smoke'))
args = parser.parse_args()
if not args.serial.startswith('emulator-'):
    raise SystemExit('This script only operates on a dedicated emulator serial.')
sdk = os.environ.get('ANDROID_HOME')
adb = str(Path(sdk)/'platform-tools/adb') if sdk else shutil.which('adb')
if not adb: raise SystemExit('Set ANDROID_HOME or put adb on PATH.')
args.output.mkdir(parents=True,exist_ok=True)
checks = []
def run(*parts, raw=False):
    result = subprocess.check_output([adb,'-s',args.serial,*parts],stderr=subprocess.STDOUT)
    return result if raw else result.decode(errors='replace')
def shell(*parts): return run('shell',*parts)
def ui():
    shell('uiautomator','dump','--compressed','/sdcard/onde-smoke.xml')
    return ET.fromstring(run('exec-out','cat','/sdcard/onde-smoke.xml',raw=True))
def click(node):
    nums = list(map(int,re.findall(r'\d+',node.get('bounds',''))))
    if len(nums)!=4: raise AssertionError('Invalid bounds')
    shell('input','tap',str((nums[0]+nums[2])//2),str((nums[1]+nums[3])//2))
    time.sleep(0.5)
def find(tree, text):
    return next((n for n in tree.iter('node') if n.get('text')==text or n.get('content-desc')==text),None)
def capture(name):
    (args.output/(name+'.png')).write_bytes(run('exec-out','screencap','-p',raw=True))
    (args.output/(name+'.xml')).write_bytes(ET.tostring(ui(),encoding='utf-8'))
def scroll_to(text):
    for _ in range(12):
        tree=ui(); node=find(tree,text)
        if node is not None: return node
        shell('input','swipe','540','1550','540','450','300'); time.sleep(0.3)
    raise AssertionError(f'Could not find {text}')

assert shell('getprop','sys.boot_completed').strip()=='1', 'Emulator still booting'
assert args.apk.is_file()
apk_hash = hashlib.sha256(args.apk.read_bytes()).hexdigest()
if args.offline_screenshots: shell('svc','wifi','disable')
run('install','-r',str(args.apk))
shell('am','force-stop','app.onde.audio')
shell('cmd','uimode','night','no')
shell('settings','put','system','system_locales','fr-FR')
shell('settings','put','global','sysui_demo_allowed','1')
shell('am','broadcast','-a','com.android.systemui.demo','-e','command','clock','-e','hhmm','0941')
shell('am','broadcast','-a','com.android.systemui.demo','-e','command','battery','-e','level','100','-e','plugged','false')
shell('am','start','-W','-n','app.onde.audio/.MainActivity')
time.sleep(0.5)
if find(ui(),'Bienvenue dans Onde') is not None:
    shell('input','keyevent','4'); time.sleep(0.5)
tree=ui()
assert find(tree,'Onde') is not None
assert not any('Shizuku' in n.get('text','') for n in tree.iter('node'))
checks.append('Release installed and main screen launched without a privileged-capture prompt')
capture('01-accueil')
click(find(tree,'Réglages'))
assert find(ui(),'Écoute') is not None
assert not any('Shizuku' in n.get('text','') for n in ui().iter('node'))
checks.append('Release settings accessible without Shizuku setup')
capture('02-reglages')
shell('input','keyevent','4'); time.sleep(0.3)
click(find(ui(),'Aide et diagnostic'))
assert find(ui(),'Bienvenue dans Onde') is not None
capture('03-aide')
click(scroll_to('Confidentialité et données'))
assert find(ui(),'Retour') is not None
assert any('Politique de confidentialité' in n.get('text','') for n in ui().iter('node'))
checks.append('Offline privacy policy reachable through in-app navigation')
capture('04-confidentialite')
shell('input','keyevent','4'); shell('input','keyevent','4')
shell('cmd','uimode','night','yes')
time.sleep(0.5)
assert find(ui(),'Onde') is not None
capture('05-accueil-sombre')
checks.append('Dark theme main screen remains accessible')
logs=run('logcat','-d','-b','crash')
(args.output/'crash-log.txt').write_text(logs)
assert 'Process: app.onde.audio' not in logs, 'App crash found; inspect crash-log.txt'
report={'device':args.serial,'api':shell('getprop','ro.build.version.sdk').strip(),'apkSha256':apk_hash,'screenshotsOffline':args.offline_screenshots,'checks':checks,
        'notTested':['HomePod or other physical receiver','Audio capture/transport end-to-end','Source music apps','Screen-off streaming']}
(args.output/'results.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n')
print(json.dumps(report,ensure_ascii=False,indent=2))
shell('cmd','uimode','night','no')

if args.offline_screenshots: shell('svc','wifi','enable')
