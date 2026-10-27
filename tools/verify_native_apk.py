#!/usr/bin/env python3
"""Check the renamed JNI library and 16 KiB ELF alignment inside an Onde APK."""
import struct
import sys
import zipfile

with zipfile.ZipFile(sys.argv[1]) as apk:
    names = apk.namelist()
    for abi in ('arm64-v8a', 'armeabi-v7a', 'x86_64'):
        prefix = 'base/' if 'base/manifest/AndroidManifest.xml' in names else ''
        name = f'{prefix}lib/{abi}/libonde_native.so'
        assert name in names, f'Missing {name}'
        data = apk.read(name)
        assert data[:4] == b'\x7fELF'
        endian = '<' if data[5] == 1 else '>'
        elf64 = data[4] == 2
        off = struct.unpack_from(endian + ('Q' if elf64 else 'I'), data, 32 if elf64 else 28)[0]
        size, count = struct.unpack_from(endian + 'HH', data, 54 if elf64 else 42)
        aligns = []
        for i in range(count):
            pos = off + i * size
            if struct.unpack_from(endian + 'I', data, pos)[0] == 1:  # PT_LOAD
                aligns.append(struct.unpack_from(endian + ('Q' if elf64 else 'I'), data, pos + (48 if elf64 else 28))[0])
        assert aligns and all(a >= 16384 for a in aligns), (name, aligns)
        for method in ('nativeCreateEncoder', 'nativeEncode', 'nativeDestroyEncoder', 'nativeDecode'):
            symbol = f'Java_app_onde_audio_audio_AlacNative_{method}'.encode()
            assert symbol in data, f'Missing JNI symbol {symbol!r}'
        print(f'{abi}: JNI symbols OK, ELF LOAD alignment {aligns}')
