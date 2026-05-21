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
## Instruction decoders

> Will be soon...