# window.postMessage

## window.postMessage について

`window.postMessage` は異なる生成元を持つページ間でデータを受け渡しするための API。  
ウィンドウ A はウィンドウ B に対して、指定したメッセージを送信することができる。  
ウィンドウ B はあらかじめイベントリスナーを設定しておくことで、ウィンドウ A からのメッセージを受信することができる。

### メッセージ送信

インターフェースは `otherWindow.postMessage(message, targetOrigin);`。  
`otherWindow` はメッセージの送信先ウィンドウ。  
`message` は送信するメッセージ。文字列である必要がある。
`targetOrigin` は `otherWindow` に期待する生成元。  
`otherWindow` の生成元と `targetOrigin` が異なる場合、イベントは到達しない。  
`targetOrigin` にはワイルドカードとして `*` を指定することが可能。


```javascript
// otherWindow.postMessage(message, targetOrigin);
otherWindow.postMessage("mofu", "*");
otherWindow.postMessage("mofu", "http://foo.mofu.poyo");
```

### メッセージ受信

あらかじめイベントリスナーを指定しておくことで、他ウィンドウからのメッセージを受信することができる。

```javascript
window.addEventListener(
    "message",
    function (e) {
        if (e.origin == "http://other.mofu.poyo") {
            console.log(e.data); // メッセージ
            console.log(e.origin); // メッセージ送信元の生成元
            console.log(e.source); // メッセージ送信元の window オブジェクト
        }
    },
    false
);
```

## 例

### iframe 間のメッセージ送受信

以下のページを設置する。

```html
<body>
    <div id="inner">Hello world!</div>
    <div><input type="button" value="Post to *" onclick="post('*')"></div>
    <div><input type="button" value="Post to foo.mofu.poyo" onclick="post('http://foo.mofu.poyo')"></div>
    <div><input type="button" value="Post to other.mofu.poyo" onclick="post('http://other.mofu.poyo')"></div>
    <script>
        function post(targetOrigin) {
            var message = document.getElementById("inner").innerText + " to " + targetOrigin;
            parent.postMessage(message, targetOrigin);
        }
    </script>
</body>
```

上記ページを iframe で読み込むページを [http://foo.mofu.poyo/postmessage/outer](http://foo.mofu.poyo/postmessage/outer) に設置する。

```html
<body>
    <div>
        <iframe src="http://other.mofu.poyo/postmessage/inner" frameborder="1" width="600" height="150" id="frame"></iframe>
    </div>
    <div id="out"></div>
    <script>
        window.addEventListener(
            "message",
            function(e) {
                console.log(e);
                if (e.origin == "http://other.mofu.poyo") {
                    document.getElementById("out").innerText = e.data;
                } else {
                    alert("Message from " + e.origin);
                }
            },
            false
        );
    </script>
</body>
```

iframe 内のボタンを押したときの挙動は以下の通り。

* 「Post to *」を押すと「Hello world! to *」が表示される
* 「Post to foo.mofu.poyo」を押すと「Hello world! to http://foo.mofu.poyo」が表示される
* 「Post to other.mofu.poyo」を押すと console にエラーが出力される。`postMessage` に渡した `targetOrigin` が不正だからである。

### postMessage を介した Web API 呼び出し

`other.mofu.poyo` に [Json API](http://other.mofu.poyo/users/334) を設置する。  

[http://other.mofu.poyo/postmessage/inner_call_api](http://other.mofu.poyo/postmessage/inner_call_api) に Json API を呼び出し結果を postMessage するためのページを設置する。

```html
<body>
    <div id="inner">実際はこの iframe は見えないようにします</div>
    <script>
        window.addEventListener(
            "message",
            function (e) {
                console.log(e);
                if (e.origin == "http://foo.mofu.poyo") {
                    var id = e.data;

                    var xhr = new XMLHttpRequest();
                    xhr.open("GET", "http://other.mofu.poyo/users/" + id);
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4 && xhr.status == 200) {
                            e.source.postMessage(xhr.responseText, "http://foo.mofu.poyo");
                        }
                    };
                    xhr.send(null);
                } else {
                    alert("Message from " + e.origin + " to the inner window.");
                }
            },
            false
        );
    </script>
</body>
```

[http://foo.mofu.poyo/postmessage/call_api](http://foo.mofu.poyo/postmessage/call_api) に、上記ページを iframe で埋め込んだページを設置する。  
このページには iframe 内のウィンドウとメッセージ交換するスクリプトが実装されている。

```html
<body>
    <div>
        <iframe src="http://other.mofu.poyo/postmessage/inner_call_api" frameborder="1" width="600" height="100" id="frame"></iframe>
    </div>
    <div>
        <p>ID  : <span id="user_id"></span></p>
        <p>Name: <span id="user_name"></span></p>
    </div>
    <input type="button" value="Click" onclick="call()">
    <div id="out"></div>
    <script>
        function call() {
            var window = document.getElementById("frame").contentWindow;
            window.postMessage("334", "http://other.mofu.poyo");
        }

        window.addEventListener(
            "message",
            function(e) {
                console.log(e);
                if (e.origin == "http://other.mofu.poyo") {
                    var user = JSON.parse(e.data);
                    console.log(user);
                    document.getElementById("user_id").innerText = user.id;
                    document.getElementById("user_name").innerText = user.name;
                } else {
                    alert("Message from " + e.origin + " to the outer window.");
                }
            },
            false
        );
    </script>
</body>
```

ボタンを押すと、ユーザー情報が画面に表示される。  
iframe により埋め込んだ `other.mofu.poyo` のページを介して、`other.mofu.poyo` 上に存在する API の結果を取得することができる。

## 参考文献

* [window.postMessage](https://developer.mozilla.org/ja/docs/Web/API/Window/postMessage)
