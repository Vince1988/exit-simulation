# Exit-Simulation

## Konfiguration

Die Konfiguration befindet sich unter [exitsim.properties](/src/main/resources/ch/bfh/exit_simulation/exitsim.properties).  
Es sind alle Farben möglich, die in der Color Klasse definiert werden: [Color JavaDoc](https://docs.oracle.com/javase/8/docs/api/java/awt/Color.html)

### Szene

Die Szene kann per XML definiert werden und über das Properties-File importiert werden.

```
<scene width="1280" height="720">
    <exit>
        <relpoint x=".99" y=".45" />
        <relpoint x="1.0" y=".45" />
        <relpoint x="1.0" y=".55" />
        <relpoint x=".99" y=".55" />
    </exit>
    <obstacle>
        <point x="448" y="252" />
        <point x="576" y="468" />
        <point x="832" y="396" />
        <point x="704" y="252" />
        <point x="640" y="216" />
    </obstacle>
    <obstacle>
        <relpoint x=".85" y=".05" />
        <relpoint x=".95" y=".10" />
        <relpoint x=".90" y=".15" />
        <relpoint x=".85" y=".10" />
    </obstacle>
</scene>
```

Die Breite und Höhe sowie die absoluten Werte werden momentan noch in Pixel angegeben.  
Jede Szene hat als erstes Element ein `exit`. Danach werden alle Hindernisse aufgelistet.  
Hindernisse und der Ausgang sind Polygone und werden mit Punkten in der Szene definiert. Diese Punkte können absolut oder relativ zur Szenengrösse sein. Bei relativen Punkten wird der Bruchteil der Breite, bzw. Höhe der Szene angegeben.
