(ns vm.opcodes)

;; ==============================
;; Opcodes
;; ==============================

(def opcodes
  {:nop  2r0000                                             ; no operation
   :add  2r0001                                             ; add  rd, rs, rt   => rd = rs + rt
   :sub  2r0010                                             ; sub  rd, rs, rt   => rd = rs - rt
   :mul  2r0011                                             ; mul  rd, rs, rt   => rd = rs * rt
   :div  2r0100                                             ; div  rd, rs, rt   => rd = rs / rt
   :and  2r0101                                             ; and  rd, rs, rt   => rd = rs & rt
   :or   2r0110                                             ; or   rd, rs, rt   => rd = rs | rt
   :xor  2r0111                                             ; xor  rd, rs, rt   => rd = rs ^ rt
   :lw   2r1000                                             ; lw   rd, addr     => rd = mem[addr]
   :sw   2r1001                                             ; sw   rs, addr     => mem[addr] = rs
   :beq  2r1010                                             ; beq  rs, rt, off  => if rs == rt: pc += off
   :bne  2r1011                                             ; bne  rs, rt, off  => if rs != rt: pc += off
   :j    2r1100                                             ; j    addr         => pc = addr
   :jal  2r1101                                             ; jal  addr         => r15 = pc, pc = addr (link reg)
   :trap 2r1110                                             ; trap code         => system call
   :hlt  2r1111                                             ; halt              => stopping the processor
   })

(def opcode->mnemonic (zipmap (vals opcodes) (keys opcodes)))