#!/usr/bin/env python
import Image,ImageChops
from vcrypt.merge import mergeCryptoShares

def main(dest_path, secret_path ,src1_path, src2_path):
    src1 = Image.open(src1_path)
    src2 = Image.open(src2_path)
    secret = Image.open(secret_path)
    
    p1,p2 = mergeCryptoShares(secret,src1,src2)
    
    pm = ImageChops.darker(p1,p2)
    p1.save(os.path.join(dest_path,'m1.png'))
    p2.save(os.path.join(dest_path,'m2.png'))
    pm.save(os.path.join(dest_path,'mm.png'))   

if __name__ == '__main__':
    import sys,os
    
    if len(sys.argv) != 5:
        print "usage: script destdir secret src1 src2" 
        sys.exit()
    
    main(*sys.argv[1:6])
    