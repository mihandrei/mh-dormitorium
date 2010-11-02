;autor: andrei mihai daniel math-inf 3
;testat tasm, td sub dosbox

;un subprogram implementat intr-un modul separat. 
;7. Se dau trei siruri de caractere. Sa se afiseze cel mai lung prefix comun 
;pentru fiecare din cele trei perechi de cate doua siruri ce se pot forma. 

;todo: prima varianta brute-force n**2
;      apoi daca mai e vreme facut cu string matching algo

ASSUME cs:_text,ds:data

data SEGMENT

data ENDS

_text SEGMENT public

start:
mov ax, data
mov ds, ax

;----


; finish it
mov ax, 4c00h
int 21h
_text ENDS

END start 
