(ns vm.opcodes)

;; ==============================
;; Opcodes
;; ==============================

(def opcodes
  {:nop  2r00000                                            ; no operation
   :add  2r00001                                            ; add  rd, rs, rt   => rd = rs + rt
   :sub  2r00010                                            ; sub  rd, rs, rt   => rd = rs - rt
   :mul  2r00011                                            ; mul  rd, rs, rt   => rd = rs * rt
   :div  2r00100                                            ; div  rd, rs, rt   => rd = rs / rt
   :and  2r00101                                            ; and  rd, rs, rt   => rd = rs & rt
   :or   2r00110                                            ; or   rd, rs, rt   => rd = rs | rt
   :xor  2r00111                                            ; xor  rd, rs, rt   => rd = rs ^ rt
   :lw   2r01000                                            ; lw   rd, addr     => rd = mem[addr]
   :sw   2r01001                                            ; sw   rs, addr     => mem[addr] = rs
   :beq  2r01010                                            ; beq  rs, rt, off  => if rs == rt: pc += off
   :bne  2r01011                                            ; bne  rs, rt, off  => if rs != rt: pc += off
   :blt  2r01100                                            ; blt  rs, rt, off  => if rs <  rt: pc += off
   :bgt  2r01101                                            ; bgt  rs, rt, off  => if rs >  rt: pc += off
   :ble  2r01110                                            ; ble  rs, rt, off  => if rs <= rt: pc += off
   :bge  2r01111                                            ; bge  rs, rt, off  => if rs >= rt: pc += off
   :bltz 2r10000                                            ; bltz rs, off      => if rs <  0:  pc += off
   :bgtz 2r10001                                            ; bgtz rs, off      => if rs >  0:  pc += off
   :j    2r10010                                            ; j    addr         => pc = addr
   :jal  2r10011                                            ; jal  addr         => r15 = pc, pc = addr (link reg)
   :trap 2r10100                                            ; trap code         => system call
   :hlt  2r10101                                            ; halt              => stopping the processor
   })

(def opcode->mnemonic (zipmap (vals opcodes) (keys opcodes)))