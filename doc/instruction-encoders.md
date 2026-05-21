# Instructions & Instruction encoders, decoders

---

---

## Instruction types

> ### R-type instructions
> #### R-type or Register instructions linked with register to register work.

**Format:** `[op][rd][rs][rt][funct]`  
**Stages:** IF → ID → EX → WB  
**Example:** `add r1, r2, r3` (r1 = r2 + r3)

**Key traits:** All operands are registers, ALU operation, no memory access.

---

> ### I-type instructions
> #### I-type or Immediate instructions work with constants and memory addresses.

**Format:** `[op][rs][rt][immediate]`  
**Stages:** IF → ID → EX → WB  
**Examples:** `lw r1, 100(r2)` (load), `addi r1, r2, 42` (add immediate)

**Key traits:** One operand is a 16‑bit constant or address offset.

---

> ### J-type instructions
> #### J-type or Jump instructions control program flow.

**Format:** `[op][target address]`  
**Stages:** IF → ID → EX  
**Example:** `j 0x1000` (jump to address)

**Key traits:** No register reads/writes, just changes the program counter (PC).

---

---

## Instruction encoders

## R-type encoding

### Format
```
│  31-27  │  26-22   │  21-17  │  16-12  │    11-0    │
┌─────────┬──────────┬─────────┬─────────┬────────────┐
│ opcode  │    rd    │   rs    │   rt    │   unused   │
│ (5-bit) │ (5-bit)  │ (5-bit) │ (5-bit) │ (12-bit)   │
└─────────┴──────────┴─────────┴─────────┴────────────┘
```

### Encoding function
```clojure
(defn encode-r-type
  
  "Encode R-type instruction (register-to-register operations)

  Args:
     op - operation mnemonic (e.g., :add, :sub, :mul)
     rd - destination register (0-31)
     rs - first source register (0-31)
     rt - second source register (0-31)

  Returns:
    32-bit integer instruction

  Example:
    (encode-r-type :add 1 2 3)  ; adds reg2 and reg3, stores in reg1
  "
  
  [op rd rs rt]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-shift-left rd 22)                          ; set rd to bits 26-22
            (bit-shift-left rs 17)                          ; set rs to bits 21-17
            (bit-shift-left rt 12))))                       ; set rt to bits 16-12
```

---

## I-type encoding

### Format
```
│  31-27  │  26-22  │  21-17  │           16-0          │
┌─────────┬─────────┬─────────┬─────────────────────────┐
│ opcode  │    a    │    b    │        immediate        │
│ (5-bit) │ (5-bit) │ (5-bit) │     (17-bit signed)     │
└─────────┴─────────┴─────────┴─────────────────────────┘
```

### Encoding function
```clojure
(defn encode-i-type
  
  "Encode I-type instruction (immediate / memory / branch operations)
   
   The meaning of fields 'a' and 'b' depends on instruction type:
   
   For loads:    (lw  a b imm)  → load reg 'a' from memory at address (reg 'b' + imm)
   For stores:   (sw  a b imm)  → store reg 'a' to memory at address (reg 'b' + imm)
   For ALU imm:  (addi a b imm) → a = b + imm
   For branches: (beq a b imm)  → if (a == b) then PC += imm
   
   Args:
     op  - operation mnemonic (:lw, :sw, :addi, :subi, :beq, :bne, etc.)
     a   - first register (destination for loads/ALU, source for branches)
     b   - second register (base address for loads/stores, source for ALU)
     imm - 17-bit signed immediate value (-65536 .. 65535)
   
   Returns:
     32-bit integer instruction
   
   Examples:
     (encode-i-type :lw 1 2 100)    ; load reg1 from mem[reg2 + 100]
     (encode-i-type :addi 1 2 42)   ; reg1 = reg2 + 42
     (encode-i-type :beq 1 2 16)    ; if (reg1 == reg2) skip 4 instructions
   "
  
  [op a b imm]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-shift-left a 22)                           ; set A to bits 26-22
            (bit-shift-left b 17)                           ; set B to bits 21-17
            (bit-and imm 0x1FFFF))))                        ; bits 16-0: immediate (17 bits)
```
---
## I-type encoding

### Format
```
│  31-27  │                    26-0                    │
┌─────────┬────────────────────────────────────────────┐
│ opcode  │                 target address             │
│ (5-bit) │                 (27-bit)                   │
└─────────┴────────────────────────────────────────────┘
```

### Encoding function
```clojure
(defn encode-j-type

  "Encode J-type instruction (unconditional jump operations)
   
   J-type instructions are used for unconditional jumps to target addresses.
   The 27-bit address is combined with the current PC's upper bits to form
   the full 32-bit jump target.
   
   For jumps:    (j addr)   → PC = {PC[31:27], addr, 00}
   For jal:      (jal addr) → PC = {PC[31:27], addr, 00}, ra = PC + 4
   
   Args:
     op   - operation mnemonic (:j, :jal)
     addr - 27-bit target address (0 .. 134,217,727)
   
   Returns:
     32-bit integer instruction
   
   Examples:
     (encode-j-type :j 0x1000)     ; jump to address 0x1000
     (encode-j-type :jal 0x2000)   ; jump and link to address 0x2000
   "

  [op addr]
  (let [opcode (get opcodes op)]                            ; get current opcode number
    (bit-or (bit-shift-left opcode OPCODE-SHIFT)            ; set opcode num to bits 31-27
            (bit-and addr 0x7FFFFFF))))                     ; bits 26-0: target address (27 bits)
```


---

---
## Instruction decoder

### Decoder overview

`decode` takes a 32-bit instruction and extracts its fields. It's the inverse of encoding:

```
encode: [op, a, b, c] → 0x12345678 (packed)
decode: 0x12345678 → {:op :add, :rd 1, :rs 2, :rt 3} (unpacked)
```

### Decoding function

```clojure
(defn decode

  "Decode a 32-bit instruction into a human-readable map structure.
   
   The function works in two steps:
     1. Extract the 5-bit opcode from bits 31-27
     2. Based on the instruction type, extract the appropriate fields
   
   Returns a map containing:
     - :op     - instruction mnemonic (:add, :lw, :beq, etc.)
     - fields  - depending on instruction type (rd/rs/rt, a/b/imm, addr, etc.)
   
   Example:
     (decode 0x08C15000)  ; returns {:op :add, :rd 1, :rs 2, :rt 3}
   "
  
  [instr]
  (let [opcode (bit-shift-right instr OPCODE-SHIFT)        ; grab first 5 bits (31-27)
        mnemonic (get opcode->mnemonic opcode)]            ; map number → mnemonic (e.g., 1 → :add)

    (case mnemonic
      ;; ============================================================
      ;; R-type instructions (register-to-register)
      ;; Format: [op][rd][rs][rt][unused:12]
      ;; ============================================================
      (:add :sub :mul :div :and :or :xor)
      {:op mnemonic
       :rd (bit-and (bit-shift-right instr 22) 0x1F)       ; bits 26-22 → destination register
       :rs (bit-and (bit-shift-right instr 17) 0x1F)       ; bits 21-17 → first source register
       :rt (bit-and (bit-shift-right instr 12) 0x1F)}      ; bits 16-12 → second source register

      ;; ============================================================
      ;; I-type instructions: Load / Store
      ;; Format: [op][a][b][imm:17]
      ;; ============================================================
      (:lw :sw)
      {:op mnemonic
       :a (bit-and (bit-shift-right instr 22) 0x1F)        ; bits 26-22 → register a
       :b (bit-and (bit-shift-right instr 17) 0x1F)        ; bits 21-17 → register b
       :imm (bit-and instr 0x1FFFF)}                       ; bits 16-0 → immediate value

      ;; ============================================================
      ;; I-type instructions: Conditional branches
      ;; Format: [op][rs][rt][offset:17]
      ;; ============================================================
      (:beq :bne :blt :bgt :ble :bge :bltz :bgtz)
      {:op mnemonic
       :rs (bit-and (bit-shift-right instr 22) 0x1F)       ; bits 26-22 → first register
       :rt (bit-and (bit-shift-right instr 17) 0x1F)       ; bits 21-17 → second register
       :offset (bit-and instr 0x1FFFF)}                    ; bits 16-0 → branch offset

      ;; ============================================================
      ;; J-type instructions: Unconditional jumps
      ;; Format: [op][addr:27]
      ;; ============================================================
      (:j :jal)
      {:op mnemonic
       :addr (bit-and instr 0x7FFFFFF)}                    ; bits 26-0 → target address

      ;; ============================================================
      ;; Trap instruction (system call)
      ;; Format: [op][code:27]
      ;; ============================================================
      (:trap)
      {:op mnemonic
       :code (bit-and instr 0x7FFFFFF)}                    ; bits 26-0 → trap code

      ;; ============================================================
      ;; No-operation and Halt (no extra fields)
      ;; ============================================================
      (:hlt :nop)
      {:op mnemonic}

      ;; ============================================================
      ;; Unknown instruction (should never happen in valid code)
      ;; ============================================================
      {:op :unknown :instr instr})))
```