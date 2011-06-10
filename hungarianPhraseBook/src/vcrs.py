#!/usr/bin/env python
import Image,ImageChops
from vcrypt.split import splitInCryptoShares

def main(dest, pth):
    src = Image.open(pth)
    p1,p2 = splitInCryptoShares(src)
    pm = ImageChops.darker(p1,p2)
    p1.save(os.path.join(dest,'p1.png'))
    p2.save(os.path.join(dest,'p2.png'))
    pm.save(os.path.join(dest,'pm.png'))   

if __name__ == '__main__':
    import sys,os
    
    if len(sys.argv) != 3:
        print "usage: script destdir srcImage" 
        sys.exit()
    
    pth = sys.argv[2]
    dest = sys.argv[1]
    main(dest,pth)
    