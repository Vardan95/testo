# Առաջարկ

Առաջարկում եմ թետերի նկարագրման եւ իրականացման կանչի հետեւյալ տարբերակը.

  ````
    module test_module_1 {
        test 1 {
           =>(w0,r0) // աճող հաջորդականությամբ գրել/կարդալ 0
           // <= - նվազող հաջորդականությամբ
        }
    }
    
    // Սահմանենք հիշողություն
    memory mem1[100][100]
    
    // Սահմանենք անսարքություն
   let mem1[50][50] st0
   let mem1[20][50] st1
    
   // Աշխատացնում ենք թեստը, լռությամբ կանցնի բոլոր թեստերի վրայով, անգամ եթե ֆեյլ է եղել
   run test_module_1 on mem1
   
   // Հնարավորություն ենք տալիս սահմանելու strict mode եթե հերթական տեստը ֆեյլ է եղել, դադարեցնել աշխատանքը.
   run strict test_module_1 on mem1
  ````
  
  Նաեւ ենթադրում եմ որ միայն թեստը աշխատել չի կարող, այն պետք է մաս կազմի ինչ որ մոդուլի, ընդ որում մոդուլը կարող է ունենալ զրո  ավել թեստեր.
  
  
````
  Program     = {(TModule|TRun|TMemoryDef|TFailureDef)}.
  Memory      = Ident'['Number']''['Number']'[NewLines].
  TMemoryDef  = 'memory' Memory[NewLines].
  FailureDef  = 'st0'|'st1'.
  TFailureDef = 'let' Memory FailureDef[NewLines].
  TRun        = 'run' ['strict'] Ident 'on' Ident[NewLines].
  TModule     = 'module' Ident [NewLines] '{' [NewLines] {Test} [NewLines] '}' [NewLines].
  Test        = 'test' Ident [NewLines] '{' [NewLines] {TestOp} [NewLines] '}' [NewLines].
  TestOp      = (=>|<=)'('Op ',' Op')'[NewLines].
  Op          = 'r0'|'r1'|'w0'|'w1'
  NewLines    = NL{NewLines}.
