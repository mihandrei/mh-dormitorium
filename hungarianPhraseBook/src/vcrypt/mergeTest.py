import unittest
from merge import _cryptoMerge,patterns

class Test(unittest.TestCase):
    def printsubpix(self,subpix):
        print subpix[0], subpix[1]
        print subpix[2], subpix[3]
        print
        
    def test_cryptoMerge_bbb(self):
        subpix1, subpix2 = _cryptoMerge(False,False,False)
        self.printsubpix(subpix1)
        self.printsubpix(subpix2)
        assert subpix1 in patterns[:2]
        assert subpix2 in patterns[:2]
        
    def test_cryptoMerge_bbw(self):
        subpix1, subpix2 = _cryptoMerge(False,False,True)
        self.printsubpix(subpix1)
        self.printsubpix(subpix2)
        assert subpix1 in patterns[:2]
        assert subpix2 in patterns[:2]

    def test_cryptoMerge_wwb(self):
        subpix1, subpix2 = _cryptoMerge(True,True,False)
        self.printsubpix(subpix1)
        self.printsubpix(subpix2)
        assert subpix1 in patterns[2:]
        assert subpix2 in patterns[2:]
if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()