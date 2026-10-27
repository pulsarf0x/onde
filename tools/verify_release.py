#!/usr/bin/env python3
"""Fail CI if the Play release contains privileged SDK components or bad metadata."""
from pathlib import Path
import sys
import xml.etree.ElementTree as ET
import zipfile

bundle = Path(sys.argv[1])
root = Path(__file__).resolve().parents[1]
manifest_file = root/'app/build/intermediates/merged_manifests/release/processReleaseManifest/AndroidManifest.xml'
manifest = ET.parse(manifest_file).getroot()
a = '{http://schemas.android.com/apk/res/android}'
assert manifest.attrib['package'] == 'app.onde.audio'
sdk = manifest.find('uses-sdk')
assert int(sdk.attrib[a+'targetSdkVersion']) >= 36
assert int(sdk.attrib[a+'minSdkVersion']) == 30
app = manifest.find('application')
assert app.attrib.get(a+'debuggable', 'false') == 'false'
assert app.attrib.get(a+'allowBackup') == 'false'
assert app.attrib.get(a+'fullBackupContent') == 'false'
assert app.attrib.get(a+'dataExtractionRules') == '@xml/data_extraction_rules'
assert all('shizuku' not in el.attrib.get(a+'name','').lower() for el in manifest.iter())
assert all('shizuku' not in el.attrib.get(a+'authorities','').lower() for el in manifest.iter())
assert app.find("activity[@"+a+"name='app.onde.audio.PrivacyActivity']") is not None
with zipfile.ZipFile(bundle) as z:
    names = z.namelist()
    assert 'base/manifest/AndroidManifest.xml' in names
    assert 'base/assets/privacy-fr.txt' in names
    assert z.read('base/assets/privacy-fr.txt') == (root/'app/src/main/assets/privacy-fr.txt').read_bytes()
    for name in names:
        if name.endswith('.dex'):
            assert b'Lrikka/shizuku/' not in z.read(name), f'Shizuku SDK found in {name}'
    assert z.testzip() is None
print('Play release: package/SDK/permissions/privacy/bundle integrity OK; no Shizuku SDK or provider.')
