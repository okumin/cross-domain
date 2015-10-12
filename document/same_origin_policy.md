# 同一生成元ポリシー(Same-origin policy)

## 同一生成元ポリシーとは

生成元 A から生成元 B の文書へアクセスことを禁じる制限。

### 同一生成元とは

以下の３つがすべて一致している場合、それらの文書は同一生成元を持つ。

* プロトコル
* ホスト
* ポート

### 禁止されること

* 生成元 A から iframe により埋め込まれた生成元 B の要素に JavaScript でアクセスすること
* 生成元 A から生成元 B の文書を Ajax で呼び出すこと

### 禁止されないこと

* script, link, img, iframe 要素などによる埋め込み

### 目的

同一生成元ポリシーがなければ以下のような悪いことができる。

* 罠ページを踏ませることで、他サイトの情報、とりわけ認証ユーザーに紐付く情報を手に入れることができる
    * 例えば、罠ページに EC サイトの個人情報設定ページを iframe で埋め込む
    * 罠ページにアクセスしたユーザーが EC サイトのセッションを持っていた場合、iframe 内に個人情報が表示される
    * 同一生成元ポリシーがなければその個人情報にアクセスできてしまう

## 例

### iframe で埋め込んだページヘのアクセス

以下の要素を持つページ([http://foo.mofu.poyo/inner](http://foo.mofu.poyo/inner))を用意する。

```html
<body>
    <div id="inner">I have a dream.</div>
</body>
```

このページを iframe により埋め込んだページを用意する。

```html
<body>
    <div>
        <iframe src="http://foo.mofu.poyo/inner" frameborder="1" width="600" height="100" id="frame"></iframe>
    </div>
    <input type="button" onclick="peek()" value="Click">
    <span id="out"></span>
    <script>
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

同一生成元である [http://foo.mofu.poyo/sop/outer](http://foo.mofu.poyo/sop/outer) からこのページにアクセスすると、埋め込んだページの要素に JavaScript を用いてアクセスできる。  
同一生成元でない [http://other.mofu.poyo/sop/outer](http://other.mofu.poyo/sop/outer) からこのページにアクセスすると、JavaScript によるアクセスがブロックされる。

### Ajax によるアクセス

[Json を返す API](http://foo.mofu.poyo/users/334) を用意する。  

この API に XMLHttpRequest でアクセスするページを用意する。

```html
<body>
    <div>
        <p>ID  : <span id="user_id"></span></p>
        <p>Name: <span id="user_name"></span></p>
    </div>
    <input type="button" onclick="call()" value="Click">
    <script>
        function call() {
            var xhr = new XMLHttpRequest();
            xhr.open("GET", "http://foo.mofu.poyo/users/334");
            xhr.onreadystatechange = function () {
                if (xhr.readyState == 4 && xhr.status == 200) {
                    try {
                        var user = JSON.parse(xhr.responseText);
                        console.log(user);
                        document.getElementById("user_id").innerText = user.id;
                        document.getElementById("user_name").innerText = user.name;
                    } catch (e) {
                        console.log(e);
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

同一生成元である [http://foo.mofu.poyo/sop/call_api](http://foo.mofu.poyo/sop/call_api) からこのページにアクセスすると、XMLHttpRequest により API を呼び出すことができる。  
同一生成元でない [http://other.mofu.poyo/sop/call_api](http://other.mofu.poyo/sop/call_api) からこのページにアクセスすると、XMLHttpRequest により呼び出した API のレスポンスを受け取ることができない。

## 参考文献

* [同一生成元ポリシー](https://developer.mozilla.org/ja/docs/Web/JavaScript/Same_origin_policy_for_JavaScript)
