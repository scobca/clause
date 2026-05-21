(ns vm.utils
  [:require [vm.constants :refer [MASK-17BIT SIGN-BIT-17]]])

;; ==============================
;; Sign extension utilities
;; ==============================

(defn sign-extend-17bit

  "Sign-extend a 17-bit value to a full 32-bit signed integer.

   If bit 16 (the 17th bit) is 1, the value is negative and gets
   all higher bits set to 1 (sign extension).

   Example:
     (sign-extend-17bit 0x1FFFF)  ; -1 → -1
     (sign-extend-17bit 1)        ; 1 → 1
     (sign-extend-17bit 0x10000)  ; 65536 → -65536"

  [val]
  (let [sign-bit (bit-and (bit-shift-right val SIGN-BIT-17) 1)]
    (if (= sign-bit 1)
      (bit-or val (bit-not MASK-17BIT))                     ; set higher bits to 1
      val)))
