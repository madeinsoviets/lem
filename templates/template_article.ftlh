<html>
    <style>
        .code,.child {
                    border-radius: 3px;
                    display: block;
                    max-width: 100%;
                    overflow: auto;
                    color: #666;
                    page-break-inside: avoid;
                    font: normal 15px/1.6 monospace;
                    padding: 1em 1.5em;
                    margin: 1.6em 0;
                    word-wrap: break-word;
                }

                .code {
                    white-space: pre-wrap;
                    background: #EBFCE3;
                }

                .text {
                }

                .link {
                }

                .picture {
                }

                .child {
                    border-radius: 3px;
                    background: #F1ECEC;
                    box-shadow: -5px 5px 10px rgba(0,0,0,0.3);
                }

                .code_child {
                    border-radius: 3px;
                    white-space: pre-wrap;
                    background: #EBFCE3;
                    padding: 1em 1.5em;
                    margin: 1.6em 0;
                }

                .text_child {
                }

                .link_child {
                }

                .picture_child {
                }
    </style>
    <head>
        <title>Welcome!</title>
    </head>
    <body>
        ${article?no_esc}
    </body>
</html>