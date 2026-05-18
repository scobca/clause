(ns vm.constants)

;; ==============================
;; Architecture constants
;; ==============================

(def MEMORY-SIZE 65536)                                     ; memory size - 64k words
(def REG-COUNT 16)                                          ; registers count - 16
(def INSTR-SIZE 4)                                          ; word size - 4

; Special addresses for memory-mapped I/O
(def IO-READ-ADDR 0xFFFFFFF0)                               ; reading a symbol
(def IO-WRITE-ADDR 0xFFFFFFF4)                              ; writing a symbol
(def IO-STATUS-ADDR 0xFFFFFFF8)                             ; device status


; Address of the interrupt vector table
(def TRAP-VECTOR-TABLE 0x00000100)