(ns vm.isa
  [:require [vm.opcodes :refer [opcodes opcode->mnemonic]]]
  [:require [vm.constants :refer [OPCODE-SHIFT MASK-5BIT MASK-17BIT MASK-27BIT]]])


;; R-type: [op:5][rd:5][rs:5][rt:5][unused:12]
(defn encode-r-type [op rd rs rt]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-shift-left rd 22)                          ; set rd to bits 26-22
            (bit-shift-left rs 17)                          ; set rs to bits 21-17
            (bit-shift-left rt 12))))                       ; set rt to bits 16-12


;; I-type: [op:5][rd/rs:5][rt:5][imm:17]
(defn encode-i-type [op a b imm]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-shift-left a 22)                           ; set A to bits 26-22
            (bit-shift-left b 17)                           ; set B to bits 21-17
            (bit-and imm MASK-17BIT))))                     ; bits 16-0: immediate (17 bits)


;; J-type: [op:5][addr:27]
(defn encode-j-type [op addr]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-and addr MASK-27BIT))))                    ; bits 26-0: address (27 bits)


;; Instruction decoder
(defn decode [instr]
  (let [opcode (bit-shift-right instr OPCODE-SHIFT)         ; get opcode mnemonic
        mnemonic (get opcode->mnemonic opcode)]

    (case mnemonic
      (:add :sub :mul :div :and :or :xor)                   ; R-type instructions
      {:op mnemonic
       :rd (bit-and (bit-shift-right instr 22) MASK-5BIT)
       :rs (bit-and (bit-shift-right instr 17) MASK-5BIT)
       :rt (bit-and (bit-shift-right instr 12) MASK-5BIT)}


      (:lw :sw)                                             ; I-type instructions
      {:op  mnemonic
       :a   (bit-and (bit-shift-right instr 22) MASK-5BIT)
       :b   (bit-and (bit-shift-right instr 17) MASK-5BIT)
       :imm (bit-and instr MASK-17BIT)}


      (:beq :bne :blt :bgt :ble :bge :bltz :bgtz)           ; I-type branch instructions
      {:op     mnemonic
       :rs     (bit-and (bit-shift-right instr 22) MASK-5BIT)
       :rt     (bit-and (bit-shift-right instr 17) MASK-5BIT)
       :offset (bit-and instr MASK-17BIT)}


      (:j :jal)                                             ; J-type instructions
      {:op   mnemonic
       :addr (bit-and instr MASK-27BIT)}


      (:trap)                                               ; J-type trap instructions
      {:op   mnemonic
       :code (bit-and instr MASK-27BIT)}


      (:hlt :nop)                                           ; halt & no operation
      {:op mnemonic}


      {:op :unknown :instr instr})))                        ; unknown instruction
