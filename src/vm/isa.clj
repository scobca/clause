(ns vm.isa
  [:require [vm.opcodes :refer [opcodes]]]
  [:require [vm.constants :refer [OPCODE-SHIFT]]])


;; R-type: [op:5][rd:5][rs:5][rt:5][unused:12]
(defn encode-r-type [op rd rs rt]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-shift-left rd 22)                          ; set rd to bits 26-22
            (bit-shift-left rs 17)                          ; set rs to bits 21-17
            (bit-shift-left rt 12))))                       ; set rt to bits 16-12


;; I-type: [op:5][rd/rs:5][rt:5][imm:17]
(defn encode-i-type
  [op a b imm]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-shift-left a 22)                           ; set A to bits 26-22
            (bit-shift-left b 17)                           ; set B to bits 21-17
            (bit-and imm 0x1FFFF))))                        ; bits 16-0: immediate (17 bits)

;; J-type: [op:5][addr:27]
(defn encode-j-type [op addr]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-and addr 0x7FFFFFF))))