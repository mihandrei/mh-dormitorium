ASSUME cs:_text,ds:data

data SEGMENT
a dw 7FFFh ; max > 0 
b dw 8000h ; min < 0
data ENDS

stiva SEGMENT stack
  db 512 dup (?)
stiva ends

_text SEGMENT public

start:
mov ax, data
mov ds, ax

mov ax, stiva
mov ss, ax

;----
;teste sa vad cum se comporta overflowu'

pushf

mov ax, a 
add ax,1        ; S,O -> 1 signed overflow , cu o in dx dx:ax i corect

popf

mov ax,b        
sub ax,1        ;undeflow o devine 1 S i 0, dx ar trebui sa fie FFFF

;=> daca fac intai un cwd in dx si poi adun/scad i ok
;ce ma fac daca in dx sunt deja high biti?
;daca se cwd-eshte un FFFF at tre adunat la dx
;daca dx e deja 8000h at avem overflow, undeva tre sa ne si oprim 
;ca daca nu facem aritmetica arbritrara
        
mov ax,-1
add ax,1  ;asta ii interesant, ax devine 0 cu Cf setat
            ;cwd l-o facut pe dx ffff , tre adaugat la dx cary-u 

;=>deci corect ii:
;adunare signed fara overflow pe 16 biti:
mov ax,-1
cwd
add ax,1 ;operatiunea
adc dx,0

;adunare pe 32 cu overfloe de 32:add lowwords apoi adc dx, highword

;adresari mem segmentata ds ss es segmente *10+16 it offset ->adr liniara
;protected mode tabele in loc de *10+16
;bx bp is baze in cadru segmentului ds respect. ss 
;si di is offseturi fata de baze
;mov [bx+si]+3,ax


; finish it
mov ax, 4c00h
int 21h
_text ENDS

END start 
