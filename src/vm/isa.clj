(ns vm.isa
  [:require [vm.opcodes :refer [opcodes opcode->mnemonic]]]
  [:require [vm.constants :refer [OPCODE-SHIFT MASK-5BIT MASK-17BIT MASK-27BIT]]]
  [:require [vm.utils :refer [sign-extend-17bit]]])


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
  (let [opcode (bit-shift-right instr OPCODE-SHIFT)
        mnemonic (get opcode->mnemonic opcode)]
    (case mnemonic

      ;; R-type instructions
      (:add :sub :mul :div :and :or :xor)
      {:op mnemonic
       :rd (bit-and (bit-shift-right instr 22) MASK-5BIT)
       :rs (bit-and (bit-shift-right instr 17) MASK-5BIT)
       :rt (bit-and (bit-shift-right instr 12) MASK-5BIT)}

      ;; I-type: Load / Store — immediate is SIGNED
      (:lw :sw)
      {:op  mnemonic
       :a   (bit-and (bit-shift-right instr 22) MASK-5BIT)
       :b   (bit-and (bit-shift-right instr 17) MASK-5BIT)
       :imm (sign-extend-17bit (bit-and instr MASK-17BIT))}

      ;; I-type: Conditional branches — offset is SIGNED
      (:beq :bne :blt :bgt :ble :bge :bltz :bgtz)
      {:op     mnemonic
       :rs     (bit-and (bit-shift-right instr 22) MASK-5BIT)
       :rt     (bit-and (bit-shift-right instr 17) MASK-5BIT)
       :offset (sign-extend-17bit (bit-and instr MASK-17BIT))}

      ;; J-type: Unconditional jumps — addr is UNSIGNED
      (:j :jal)
      {:op   mnemonic
       :addr (bit-and instr MASK-27BIT)}

      ;; Trap instruction — code is UNSIGNED
      (:trap)
      {:op   mnemonic
       :code (bit-and instr MASK-27BIT)}

      ;; No-operation and Halt
      (:hlt :nop)
      {:op mnemonic}

      ;; Unknown instruction
      {:op :unknown :instr instr})))
