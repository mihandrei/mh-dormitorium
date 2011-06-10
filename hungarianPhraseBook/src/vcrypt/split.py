import Image, random
#from Crypto.Util import randpool
def _coinflip():
    return bool(random.randint(0,1))

def _cryptoSplit(white):
    subpix1 = _coinflip()
    subpix2 = subpix1 if white else not subpix1
    return subpix1, subpix2
        
def _writeSubpixel(pix,i,j,left):
    pix[i,j]=left
    pix[i,j+1]=left
    pix[i+1,j]= not left
    pix[i+1,j+1]=not left    
        
def splitInCryptoShares(src):
    w,h = src.size
    spix = src.convert('1').load()

    p1 = Image.new('1', (w*2, h*2))
    p2 = Image.new('1', (w*2, h*2))
    p1_pix = p1.load()
    p2_pix = p2.load()
    
    for j in xrange(h):
        for i in xrange(w):
            subpix1, subpix2 = _cryptoSplit(spix[i,j])
            _writeSubpixel(p1_pix, i*2, j*2, subpix1)
            _writeSubpixel(p2_pix, i*2, j*2, subpix2)
    
    return p1,p2