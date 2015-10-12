# document.domain

## document.domain とは

JavaScript から現在の文書のホストを取得、設定するためのプロパティ。  
document.domain に設定するドメインは、現在の文書のスーパードメインでなければならない。  
これにより、同一生成元ポリシーの制約を緩めることができる。

### 同一生成元ポリシーの制約を緩められるケース

* 文書 A と文書 B の document.domain が共に同一の値に再設定されている場合

### 同一生成元ポリシーの制約を緩められないケース

* 文書 A と文書 B のうち一方の `document.domain` のみが再設定されている場合
    * もともとホストが「example.com」だった文書と `document.domain` によりホストが「example.com」に再設定された文書は同一生成元であるとみなされない
* プロトコル(スキーム)が異なる場合
* Ajax によりアクセスする場合
    * 古いブラウザだと XMLHttpRequest できちゃうケースもあるらしいが、基本ダメ

### 利用可能な環境

多分あらゆるブラウザで利用可能。

## 例

### iframe によるアクセス

以下の要素を持つページ([http://foo.mofu.poyo/domain/inner](http://foo.mofu.poyo/domain/inner))を用意する。  
`document.domain` をスーパードメイン(mofu.poyo)に再設定する。

```html
<body>
    <div id="inner">We are happy!</div>
    <script>
        document.domain = "mofu.poyo";
    </script>
</body>
```

このページを iframe により埋め込んだページを用意する。  
`document.domain` をスーパードメイン(mofu.poyo)に再設定する。

```html
<body>
    <div>
        <iframe src="http://foo.mofu.poyo/domain/inner" frameborder="1" width="600" height="100" id="frame"></iframe>
    </div>
    <input type="button" onclick="peek()" value="Click">
    <span id="out"></span>
    <script>
        document.domain = "mofu.poyo";
        function peek() {
            var inner = document.getElementById("frame").contentWindow.document.getElementById("inner");
            document.getElementById("out").innerText = inner.innerText;
        }
    </script>
</body>
```

iframe の内側と同じ `document.domain` を設定しているため、以下のいずれのドメインからでも iframe 内の要素にアクセスできる。

* [http://foo.mofu.poyo/domain/outer](http://foo.mofu.poyo/domain/outer)
* [http://other.mofu.poyo/domain/outer](http://other.mofu.poyo/domain/outer)
* [http://mofu.poyo/domain/outer](http://mofu.poyo/domain/outer)

### ホストを合わせるだけじゃダメ

以下の要素を持つページ([http://mofu.poyo/inner](http://mofu.poyo/inner))を用意する。

```html
<body>
    <div id="inner">I have a dream.</div>
</body>
```

このページを iframe により埋め込んだページを用意する。  
`document.domain` を iframe により読み込むページのドメインと同じ値に再設定する。

```html
 <body>
    <div>
        <iframe src="http://mofu.poyo/inner" frameborder="1" width="600" height="100" id="frame"></iframe>
    </div>
    <input type="button" onclick="peek()" value="Click">
    <span id="out"></span>
    <script>
        document.domain = "mofu.poyo";
        function peek() {
            try {
                var inner = document.getElementById("frame").contentWindow.document.getElementById("inner");
                document.getElementById("out").innerText = inner.innerText;
            } catch (e) {
                alert(e.message);
            }
        }
    </script>
</body>
```

`document.domain` を iframe の内側と同じホストに再設定したが、同一生成元であるとみなされないためエラーとなる。

* [http://foo.mofu.poyo/domain/outer_ng](http://foo.mofu.poyo/domain/outer_ng)
* [http://mofu.poyo/domain/outer_ng](http://mofu.poyo/domain/outer_ng)

### Ajax によるアクセスは不可

Json を返す API を用意する。  
ホストは「mofu.poyo」とする。  

この API に XMLHttpRequest でアクセスするページを用意する。  
`document.domain` を API のホストと同じ「mofu.poyo」に再設定する。

```html
<body>
    <div>
        <p>ID  : <span id="user_id"></span></p>
        <p>Name: <span id="user_name"></span></p>
    </div>
    <input type="button" onclick="call()" value="Click">
    <script>
        function call() {
            document.domain = "mofu.poyo";
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "http://mofu.poyo/users/334");
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    try {
                        var user = JSON.parse(xhr.responseText);
                        console.log(user);
                        document.getElementById("user_id").innerText = user.id;
                        document.getElementById("user_name").innerText = user.name;
                    } catch (e) {
                        alert(e.message);
                    }
                }
            };
            xhr.onerror = function (e) {
                console.error(e);
                console.error(xhr);
                alert("Error!");
            };
            xhr.send(null);
        }
    </script>
</body>
```

XMLHttpRequest による呼び出しは `document.domain` の影響を受けない。

[http://foo.mofu.poyo/domain/call_api](http://foo.mofu.poyo/domain/call_api) から XMLHttpRequest で API を呼び出すと失敗する。  
[http://mofu.poyo/domain/call_api](http://mofu.poyo/domain/call_api) から XMLHttpRequest で API を呼び出すと成功する。

## 参考文献

* [Document.domain](https://developer.mozilla.org/en-US/docs/Web/API/Document/domain)
