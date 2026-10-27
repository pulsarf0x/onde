#!/usr/bin/env python3
"""Generate a persistent upload key and a private shell environment; never print secrets."""
import os
from pathlib import Path
import secrets
import shlex
import shutil
import subprocess
import sys

folder = Path(sys.argv[1] if len(sys.argv) > 1 else '.signing').resolve()
folder.mkdir(parents=True, exist_ok=True, mode=0o700)
key = folder / 'onde-upload.p12'
env_file = folder / 'signing.env'
if key.exists() or env_file.exists():
    raise SystemExit('Refusing to overwrite an existing key or signing environment.')
java_home = os.environ.get('JAVA_HOME')
keytool = str(Path(java_home) / 'bin/keytool') if java_home else shutil.which('keytool')
if not keytool:
    raise SystemExit('Set JAVA_HOME to a JDK 17 installation first.')
password = secrets.token_urlsafe(40)
values = {'ONDE_UPLOAD_STORE_FILE':str(key), 'ONDE_UPLOAD_STORE_PASSWORD':password,
          'ONDE_UPLOAD_KEY_ALIAS':'onde-upload', 'ONDE_UPLOAD_KEY_PASSWORD':password}
subprocess.run([keytool, '-genkeypair', '-keystore', str(key), '-storetype', 'PKCS12',
               '-alias', 'onde-upload', '-keyalg', 'RSA', '-keysize', '4096', '-validity', '10000',
               '-dname', 'CN=Onde Upload', '-storepass:env', 'ONDE_UPLOAD_STORE_PASSWORD',
               '-keypass:env', 'ONDE_UPLOAD_KEY_PASSWORD', '-noprompt'],
              env={**os.environ, **values}, check=True, stdout=subprocess.DEVNULL)
os.chmod(key, 0o600)
fd = os.open(env_file, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
with os.fdopen(fd,'w') as f:
    for name, value in values.items(): f.write(f'export {name}={shlex.quote(value)}\n')
print(f'Upload key created in {folder}. Back up the key and signing.env privately.')
