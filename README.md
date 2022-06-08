### promlexer
promlexer is a tool to tokenize [prometheus format](https://prometheus.io/docs/concepts/data_model/) text (metrics)  built with [JFlex](https://jflex.de/). 
It's basically based on the prometheus [promlex.l](https://github.com/prometheus/prometheus/blob/main/model/textparse/promlex.l). 
But unlike the Prometheus, it drops the lines begin with a character `#`, e.g., HELP lines and TYPE lines.
You can run the following maven command to build your lexer:
```shell
mvn clean package
```
There is also a [demo](./src/main/java/promparse/PromParser.java) shows a way of using this little lexer.
### License
There is **NO WARRANTY** for promlexer, its code and its documentation.

### JFlex vs Antlr
![](https://journals.plos.org/plosone/article/figure/image?size=inline&id=10.1371/journal.pone.0264326.t002)

According to this paper[<sup>[1]</sup>](#R1), Antlr has an O(n<sup>4</sup>) theoretical computation complexity while JFlex has an O(n) one. So I choose JFlex over Antlr.


### References
<div><a name="R1"></a>
[1] Ortin, F., Quiroga, J., Rodriguez-Prieto, O. and Garcia, M., 2022. An empirical evaluation of Lex/Yacc and ANTLR parser generation tools. Plos one, 17(3), p.e0264326.
</div>