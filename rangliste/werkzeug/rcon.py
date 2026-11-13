#!/usr/bin/env python3
"""Minimaler RCON-Client für den Rauchtest. Aufruf: rcon.py HOST PORT PASSWORT BEFEHL"""
import socket
import struct
import sys

LOGIN, BEFEHL = 3, 2


def paket(i, typ, text):
    # Little-Endian: Länge, ID, Typ, Text, zwei Nullbytes
    daten = struct.pack('<ii', i, typ) + text.encode('utf-8') + b'\x00\x00'
    return struct.pack('<i', len(daten)) + daten


def genau(sock, n):
    puffer = b''
    while len(puffer) < n:
        stueck = sock.recv(n - len(puffer))
        if not stueck:
            raise ConnectionError('RCON-Verbindung vorzeitig geschlossen')
        puffer += stueck
    return puffer


def lies_paket(sock):
    laenge = struct.unpack('<i', genau(sock, 4))[0]
    daten = genau(sock, laenge)
    i, typ = struct.unpack('<ii', daten[:8])
    return i, typ, daten[8:-2].decode('utf-8', 'replace')


def main():
    if len(sys.argv) != 5:
        print(__doc__, file=sys.stderr)
        sys.exit(2)
    host, port, passwort, befehl = sys.argv[1], int(sys.argv[2]), sys.argv[3], sys.argv[4]
    with socket.create_connection((host, port), timeout=15) as sock:
        sock.sendall(paket(1, LOGIN, passwort))
        antwort_id, _, _ = lies_paket(sock)
        if antwort_id == -1:
            print('falsches RCON-Passwort', file=sys.stderr)
            sys.exit(3)
        sock.sendall(paket(2, BEFEHL, befehl))
        _, _, text = lies_paket(sock)
        print(text)


if __name__ == '__main__':
    main()
