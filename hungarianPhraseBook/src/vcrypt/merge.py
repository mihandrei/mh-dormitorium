import Image, random

#b1 b2 w1 w2 w3 w4
#patterns = ((0,1,1,1), (1,1,1,0), (1,1,0,0), (0,1,0,1), (0,0,1,1), (1,0,1,0)) 
patterns = ((1,0,0,0), (0,0,0,1), (0,0,1,1), (1,0,1,0), (1,1,0,0), (0,1,0,1)) 
#(0,4,3) inseamna b1 e legat de b1 w3 w2
#(1,5) inseamna w1 e legat b2, w4
#indecsi corespund la b1 b2 w1 w2 w3 w4  
incidence_white = ((0,4,3),(1,2,5),(1,5),(0,4),(0,3),(1,2))
incidence_black = ((1,2,5),(0,4,3),(0,4),(1,5),(1,2),(0,3))

def _cryptoMerge(share1_white, share2_white, dest_white):
    #select primul nod/subpixel pattern
    if share1_white:
        idx = random.randint(2,5)
    else:
        idx = random.randint(0,1)
    
    #select subpix care pot da dest-white cu pattern[idx]
    if dest_white:
        incident = incidence_white[idx]
    else:
        incident = incidence_black[idx]
    
    #select un subpix care are culoarea din share2
    if share2_white:
        if len(incident) == 3:
            incident_idx = random.randint(1,2)
        else:
            incident_idx = 1
    else:
        incident_idx=0
        
    idx2 = incident[incident_idx]
    
    subpixel1 = patterns[idx]
    subpixel2 = patterns[idx2]
    return subpixel1, subpixel2
        
def _writeSubpixels(share, i, j, subpix):
    share[i,j]=subpix[0]
    share[i,j+1]=subpix[1]
    share[i+1,j]= subpix[2]
    share[i+1,j+1]=subpix[3]  
'''
makes 2 images that look like src1 src2 but superimposed reveal the secret Image
'''
def mergeCryptoShares(secret, src1, src2):
    if not secret.size == src1.size == src2.size:
        raise ValueError('secret and sources have to be the same size')
    w,h = secret.size
    #load source images & conver to black white
    secret_pix = secret.convert('1').load()
    src1_pix = src1.convert('1').load()
    src2_pix = src2.convert('1').load()
    
    #destinations
    share1 = Image.new('1', (w*2,h*2));
    share2 = Image.new('1', (w*2,h*2));
    share1_pix = share1.load();
    share2_pix = share2.load();
    
    for j in xrange(h):
        for i in xrange(w):
            subpix1, subpix2 = _cryptoMerge(src1_pix[i,j], src2_pix[i,j], secret_pix[i,j])
            _writeSubpixels(share1_pix, i*2, j*2, subpix1)
            _writeSubpixels(share2_pix, i*2, j*2, subpix2)
    
    return share1,share2