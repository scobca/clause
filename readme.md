# clause-lang

Clojure-based educational processor, assembler, and emulator for ITMO Computer Architecture course (Lab 4).

**Variant:** `lisp | risc | neum | hw | tick | binary | trap | mem | cstr | prob1 | cache`

## Author
- **Name:** Фокин Владимир | Fokin Vladimir
- **Group:** P3223

## Overview

This project implements a complete computing system from scratch:
- **High‑level language:** a Lisp dialect (S-expressions)
- **Translator (Compiler):** Lisp → RISC‑like binary machine code
- **Processor model:** tick‑accurate, hardware‑controlled (hardwired), with a data cache
- **Memory model:** Neumann architecture (shared code/data), memory‑mapped I/O
- **I/O model:** trap‑based interrupts
- **String representation:** C‑style null‑terminated strings (`cstr`)
- **Target algorithm:** Project Euler Problem 1 (Multiples of 3 or 5)


## Features

- RISC instruction set with fixed‑length instructions
- Binary machine code output
- Tick‑accurate simulation (can be paused after any tick)
- Data cache with configurable latency (cache = 1 tick, RAM = 10 ticks)
- Trap‑handling for character input/output
- Memory‑mapped I/O devices
- Golden tests for all required algorithms
- CI with linting and formatting (ruff/mypy equivalent for Clojure: `cljstyle`, `clj-kondo`)

## Project Structure

```
clause-lang/
│
├── src/
│ │
│ ├── compiler/ # Lisp → machine code translator
│ │ ├── parser.clj # S‑expression parser
│ │ ├── ast.clj # AST definitions
│ │ ├── codegen.clj # Machine code generation
│ │ └── utils.clj # Helper utilities
│ │
│ ├── vm/ # Processor model
│ │ ├── cpu.clj # Main CPU loop (tick‑accurate)
│ │ ├── alu.clj # ALU operations
│ │ ├── registers.clj # Register file (RISC)
│ │ ├── memory.clj # Neumann memory (code + data)
│ │ ├── cache.clj # Data cache implementation
│ │ ├── trap.clj # Trap / interrupt handling
│ │ ├── io.clj # Memory‑mapped I/O + buffers
│ │ └── decoder.clj # Instruction decoder
│ │
│ └── cli/ # Command‑line interfaces
│   ├── compile.clj # Compiler entry point
│   └── run.clj # Emulator entry point
│
├── tests/
│ │
│ ├── golden/ # Golden tests
│ │ ├── hello # "Hello, World!" test
│ │ ├── cat # Echo / cat test
│ │ ├── hello_name # Interactive name greeting
│ │ ├── sort # Sorting algorithm test
│ │ ├── prob1 # Euler Problem #1
│ │ └── cache_demo # Cache performance demo
│ │
│ └── test_runner.clj # Test harness / runner
│
├── examples/ # Example Lisp programs
│
├── .github/workflows/ # CI/CD pipelines (lint, test)│
├── README.md # This file
└── project.clj # Clojure project dependencies & build
```

**Legend:**
- `compiler/` – translates Lisp source → binary machine code
- `vm/`      – tick‑accurate processor simulator with cache & trap I/O
- `cli/`     – user‑facing compiler / runner commands
- `tests/`   – golden tests for all required algorithms
- `examples/`– sample programs in the Lisp dialect

## Implementation Status

| Module                       | Status         | Notes                                                                 |
|------------------------------|----------------|-----------------------------------------------------------------------|
| **Language (Lisp dialect)**  | 🟡 In progress | S‑expressions, `if`, `defn`, `loop/recur`, `print`, `read`            |
| **Translator (Compiler)**    | 🟡 In progress | Three‑pass: parse → AST → linearize → binary emit                     |
| **Instruction Set (RISC)**   | 🔲 Planned     | 16 fixed‑length instructions (add, sub, lw, sw, beq, jal, trap, etc.) |
| **Processor Model (tick)**   | 🔲 Planned     | Cycle‑accurate main loop, state exposed after every tick              |
| **Control Unit (hardwired)** | 🔲 Planned     | Decoder + hardwired control signals                                   |
| **Memory (neum)**            | 🔲 Planned     | Shared 64K words (code + data)                                        |
| **Cache**                    | 🔲 Planned     | Direct‑mapped, 8 lines, LRU, 1‑tick hit, 10‑tick miss                 |
| **Trap I/O**                 | 🔲 Planned     | Interrupt vector table, `trap` instruction, interrupt handler         |
| **Memory‑mapped I/O**        | 🔲 Planned     | Input / output mapped to fixed addresses (e.g., `0xFF00`, `0xFF01`)   |
| **C strings (cstr)**         | 🔲 Planned     | Null‑terminated string literals in data section                       |
| **Golden tests**             | 🔲 Planned     | 6 algorithms (hello, cat, hello_name, sort, prob1, cache_demo)        |
| **CI (lint + test)**         | 🔲 Planned     | GitHub Actions + `cljstyle` + `clj‑kondo` + golden tests              |

## Key Design Decisions (Variant Justification)

### Language: Lisp dialect
- Everything is an expression (no distinction between statements/expressions)
- Supports recursive functions (tail‑call optimization not required)
- `if` can be used anywhere: `(print (if (= x 1) "one" "other"))`
- Minimal built‑ins: `+`, `-`, `*`, `/`, `=`, `<`, `>`, `print`, `read`

### Architecture: RISC
- 16 general‑purpose registers (`r0` = always zero)
- Fixed 32‑bit instructions
- Only `lw`/`sw` access memory (load‑store architecture)
- ALU operations work only on registers

### Memory: Neumann (`neum`)
- One address space for both code and data (simplicity)
- Program starts at `0x0000`
- Interrupt vector table at fixed location (e.g., `0x0010`)

### Control Unit: Hardwired (`hw`)
- Decoder directly generates control signals
- Simpler to implement and debug

### Simulation accuracy: Tick (`tick`)
- CPU state can be inspected after each clock cycle
- Useful for debugging cache effects

### Machine code: Binary (`binary`)
- Real binary files (not text 0/1)
- Accompanying debug listing shows `address - hexdump - disassembly`

### I/O model: Trap (`trap`)
- `trap` instruction triggers software interrupt
- Interrupt handler reads from/writes to memory‑mapped I/O ports
- Nested interrupts: disabled during handler (simple model)

### I/O addressing: Memory‑mapped (`mem`)
- Input device at `0xFFFFFFF0` (read char)
- Output device at `0xFFFFFFF4` (write char)
- Status registers at `0xFFFFFFF8` (ready bits)

### Strings: C‑style (`cstr`)
- Zero‑terminated: `"Hello"` → `'H','e','l','l','o',0`
- String literals placed in `.data` section

### Algorithm: Euler Problem 1 (`prob1`)
> Find the sum of all multiples of 3 or 5 below 1000.
- Uses loops, conditionals, arithmetic
- Input: none; Output: integer result

### Extension: Cache (`cache`)
- Direct‑mapped data cache, 8 lines, 16 bytes per line
- Hit = 1 tick, Miss = 10 ticks (memory access latency)
- Cache behavior logged in simulation trace
- Demonstrates performance improvement with data locality


### Requirements
- Java 21
- Clojure CLI (or `lein`)
- Faith in God and this project 🙏🏻

## License

Copyright © 2026 Fokin Vladimir (scobca)

This program and the accompanying materials are made available under the terms
of the Eclipse Public License v. 2.0 which is available at
https://www.eclipse.org/legal/epl-2.0.

SPDX-License-Identifier: EPL-2.0