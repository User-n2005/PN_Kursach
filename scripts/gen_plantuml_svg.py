import pathlib
import urllib.request
import zlib

SRC = pathlib.Path('docs/architecture_mvvm_component.puml')
OUT_SVG = pathlib.Path('docs/architecture_mvvm_component.svg')

alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_"

def encode6bit(b: int) -> str:
    if b < 0:
        b += 64
    return alphabet[b]

def append3bytes(b1: int, b2: int, b3: int) -> str:
    c1 = b1 >> 2
    c2 = ((b1 & 0x3) << 4) | (b2 >> 4)
    c3 = ((b2 & 0xF) << 2) | (b3 >> 6)
    c4 = b3 & 0x3F
    return "".join([
        encode6bit(c1 & 0x3F),
        encode6bit(c2 & 0x3F),
        encode6bit(c3 & 0x3F),
        encode6bit(c4 & 0x3F),
    ])

def encode64(data: bytes) -> str:
    r = []
    i = 0
    n = len(data)
    while i < n:
        if i + 2 == n:
            r.append(append3bytes(data[i], data[i + 1], 0))
            i += 2
        elif i + 1 == n:
            r.append(append3bytes(data[i], 0, 0))
            i += 1
        else:
            r.append(append3bytes(data[i], data[i + 1], data[i + 2]))
            i += 3
    return "".join(r)

def plantuml_encode(text: str) -> str:
    data = text.encode('utf-8')
    # PlantUML server expects raw DEFLATE stream (zlib wrapper stripped)
    z = zlib.compress(data, 9)
    raw_deflate = z[2:-4]
    return encode64(raw_deflate)

puml = SRC.read_text(encoding='utf-8-sig')
# Prefer compressed encoding, but some environments may produce incompatible streams.
# Fall back to hex encoding (~h) which requires no compression algorithm agreement.
try:
    encoded = plantuml_encode(puml)
    url = f"https://www.plantuml.com/plantuml/svg/{encoded}"
    with urllib.request.urlopen(url) as resp:
        svg = resp.read()
except Exception:
    encoded = "~h" + puml.encode("utf-8").hex()
    url = f"https://www.plantuml.com/plantuml/svg/{encoded}"
    with urllib.request.urlopen(url) as resp:
        svg = resp.read()

OUT_SVG.write_bytes(svg)
print('OK')
print('SVG saved:', OUT_SVG)
print('Source URL:', url)
