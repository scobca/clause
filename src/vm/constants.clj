(ns vm.constants)

;; ==============================
;; Architecture constants
;; ==============================

;; Memory
(def MEMORY-SIZE 65536)                                     ; 64K words
(def WORD-SIZE 32)                                          ; 32 bits per word


;; Registers
(def REG-COUNT 32)                                          ; r0-r31, r0 is always zero
(def LINK-REGISTER 31)                                      ; r31 is used as link register for jal


;; Instruction encoding
(def OPCODE-SIZE 5)                                         ; 5 bits for opcode (32 possible instructions)
(def OPCODE-SHIFT 27)                                       ; 32 - 5 = 27 (shift amount for encoding)


;; Bit masks for instruction decoding
(def MASK-5BIT 0x1F)                                        ; 5-bit mask (11111) for registers
(def MASK-17BIT 0x1FFFF)                                    ; 17-bit mask for immediate/offset
(def MASK-27BIT 0x7FFFFFF)                                  ; 27-bit mask for address/trap code


;; Special addresses for memory-mapped I/O
(def IO-READ-ADDR 0xFFFFFFF0)                               ; reading a character
(def IO-WRITE-ADDR 0xFFFFFFF4)                              ; writing a character
(def IO-STATUS-ADDR 0xFFFFFFF8)                             ; device status (0 = busy, 1 = ready)

;; Interrupts
(def TRAP-VECTOR-TABLE 0x00000100)                          ; where trap handlers start