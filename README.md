## tools-cli

Simple tools for building CLI apps and interacting with the CLI, and
building a consistent CLI experience across projects.

TODO package

### Usage

[`sparkfund.cli.prompts`](TODO)

```clj
sparkfund.cli.prompts> (choice "please pick a flavor" {"vanilla" 1 "chocolate" 2})
please pick a flavor ✏️  raspberry
expecting one of: (vanilla chocolate)
please pick a flavor ✏️  chocolate
2
```

[`sparkfund.cli.shell`](TODO)

```clj
sparkfund.cli.shell> (sh! "cowsay" "-f" "flaming-sheep" "example")
[sh!] cowsay -f flaming-sheep example
 _________ 
< example >
 --------- 
  \            .    .     .   
   \      .  . .     `  ,     
    \    .; .  : .' :  :  : . 
     \   i..`: i` i.i.,i  i . 
      \   `,--.|i |i|ii|ii|i: 
           UooU\.'@@@@@@`.||' 
           \__/(@@@@@@@@@@)'  
                (@@@@@@@@)    
                `YY~~~~YY'    
                 ||    ||     
{:out
 " _________ \n< example >\n --------- \n  \\            .    .     .   \n   \\      .  . .     `  ,     \n    \\    .; .  : .' :  :  : . \n     \\   i..`: i` i.i.,i  i . \n      \\   `,--.|i |i|ii|ii|i: \n           UooU\\.'@@@@@@`.||' \n           \\__/(@@@@@@@@@@)'  \n                (@@@@@@@@)    \n                `YY~~~~YY'    \n                 ||    ||     \n",
 :err "",
 :exit 0}
sparkfund.cli.shell> (sh! "echo" "secret" {:print-cmd? false :print-out? false})
{:out "secret\n", :err "", :exit 0}
```

[`sparkfund.cli.style`](TODO)

```clj
sparkfund.cli.style> (git-branch-ok "feature/good")
"[1m[35mfeature/good[0m"
sparkfund.cli.style> (wrap-with-emoji <tada> "finished")
"🎉 🎉 🎉  finished 🎉 🎉 🎉 "
```

### License

Copyright © Sparkfund 2020

Distributed under the Apache License, Version 2.0. See LICENSE for details.
