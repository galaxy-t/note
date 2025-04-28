# pipeline

## sentiment-analysis (情感分析)

以下是一个快速使用 pipeline 快速启动模型并使用模型对输入的内容进行情感分析的例子

```python
classifier = pipeline("sentiment-analysis")
result = classifier("I've been waiting for a HuggingFace course my whole life.")
print(result)
```

```
[{'label': 'POSITIVE', 'score': 0.9598047137260437}]
```

我们也可以一次性多输入几个句子

```python
classifier(
    ["I've been waiting for a HuggingFace course my whole life.", "I hate this so much!"]
)
```

```
[{'label': 'POSITIVE', 'score': 0.9598047137260437},
 {'label': 'NEGATIVE', 'score': 0.9994558095932007}]
```

默认情况下, 此 pipeline 加载选定的(`checkpoint`)预训练模型, 该模型已针对英语情感分析进行了微调. 使用 pipeline 创建的对象,
模型将被下载和缓存.
如果重新运行该命令, 将使用缓存的模型, 无需重复下载.

将一些文本传递到 pipeline 时涉及三个主要步骤:

1. 文本被预处理为模型可以理解的格式.
2. 将预处理后的结果传递给模型.
3. 对模型的预测进行后续处理, 并输出人类可以理解的结果.

## zero-shot-classification (零样本分类)

用于对没有标签的文本进行分类

下面的示例展示了模型对输入的文本进行分类, 分类标签设置为: `education`,`politics`,`business` 这三个

```python
classifier = pipeline("zero-shot-classification")
result = classifier(
    "This is a course about the Transformers library",
    candidate_labels=["education", "politics", "business"],
)
print(result)
```

```
{'sequence': 'This is a course about the Transformers library',
 'labels': ['education', 'business', 'politics'],
 'scores': [0.8445963859558105, 0.111976258456707, 0.043427448719739914]}
```

## text-generation (文本生成)

主要使用方法为, 提供一些文本, 模型将生成剩余的文本补全整段话.

文本生成具有随机性, 如果多次生成没有得到相同的结果是正常的.

```python
generator = pipeline("text-generation")
result = generator("In this course, we will teach you how to",
                   num_return_sequences=2,
                   max_length=15, )
print(result)
```

```
[{'generated_text': 'In this course, we will teach you how to understand and use '
                    'data flow and data interchange when handling user data. We '
                    'will be working with one or more of the most commonly used '
                    'data flows — data flows of various types, as seen by the '
                    'HTTP'}]
```

上面的例子中, `num_return_sequences` 用于控制生成几个句子, `max_length` 用于控制每个句子生成几个单子.

上面的例子使用的是默认模型(`checkpoint`), 我们也可以从 Hub 中选定一个特定模型, 将其用于特定任务, 代码如下:

```python
generator = pipeline("text-generation", model="uer/gpt2-chinese-cluecorpussmall")
result = generator(
    "我是中国人",
    max_length=100,
    num_return_sequences=2,
)
print(result)
```

```
[{'generated_text': '我是中国人 金 庸 是 中 国 人 的 好 朋 友 先 生 不 仅 在 他 的 书 中 反 复 在 强 调 中 国 人 的 一 种 普 遍 的 精 神 与 自 觉 。 
先 生 的 文 章 读 过 之 后 我 认 为 中 国 人 的 精 神 是 自 觉 的 。 在 本 书 第 一 章 那 篇 文 章 之 后 再 翻 看 其 他 版 本 的 中 国 文 化 与 西 方 文 化'}, 
{'generated_text': '我是中国人 外 国 人 的 说 法 也 很 有 些 道 理 。 我 就 买 了 一 套 。 书 写 的 十 分 好 适 合 中 国 人 很 有 启 发 力 。 
书 的 排 版 很 好 翻 译 的 也 不 错 。 送 货 很 快 这 点 非 常 好 。 我 觉 得 一 定 要 买 一 套 中 国 的 史 书 。 建 议 买 中 国 的 名 著 建 议 这 个 价 钱'}]
```

## fill-mask (完形填空)

主要用于填补给定文本中的空白

```python
unmasker = pipeline("fill-mask")
result = unmasker("This course will teach you all about <mask> models.", top_k=2)
print(result)
```

```
[{'sequence': 'This course will teach you all about mathematical models.',
  'score': 0.19619831442832947,
  'token': 30412,
  'token_str': ' mathematical'},
 {'sequence': 'This course will teach you all about computational models.',
  'score': 0.04052725434303284,
  'token': 38163,
  'token_str': ' computational'}]
```

参数 `top_k` 控制用于生成多少种结果, 其中 `<mask>` 在输入的文本中作为占位符存在, 它通常被称为 `mask token`.

不同的 `fill-mask` 模型的 `mask token` 可能不同.

## ner (命名实体识别)

从输入的文本中找到哪些部分为诸如 人员,位置或组织 之类的实体

```python
ner = pipeline("ner", grouped_entities=True)
result = ner("My name is Sylvain and I work at Hugging Face in Brooklyn.")
print(result)
```

```
[{'entity_group': 'PER', 'score': 0.99816, 'word': 'Sylvain', 'start': 11, 'end': 18}, 
 {'entity_group': 'ORG', 'score': 0.97960, 'word': 'Hugging Face', 'start': 33, 'end': 45}, 
 {'entity_group': 'LOC', 'score': 0.99321, 'word': 'Brooklyn', 'start': 49, 'end': 57}]
```

以上结果, 模型正确地识别出 Sylvain 是一个人(PER), Hugging Face 是一个组织(ORG), 而 Brooklyn 是一个位置(LOC)

参数 `grouped_entities=True` 告诉 `pipeline` 将与同一实体对应的句子部分重新分组, 这里模型正确地将 `Hugging``和Face`
分组为一个组织, 即使名称由多个词组成.

## question-answering (问答)

问答, 使用给定上下文中的信息回答问题

`注: 问答不是给模型一个问题, 模型回答一个答案, 而是给模型段内容, 然后根据这段内容向模型提问, 模型从这段内容中自己找到答案.`

```python
question_answerer = pipeline("question-answering", model="uer/roberta-base-chinese-extractive-qa")
result = question_answerer(
    question="著名诗歌《假如生活欺骗了你》的作者是",
    context="普希金从那里学习人民的语言，吸取了许多有益的养料，这一切对普希金后来的创作产生了很大的影响。这两年里，普希金创作了不少优秀的作品，如《囚徒》、《致大海》、《致凯恩》和《假如生活欺骗了你》等几十首抒情诗，叙事诗《努林伯爵》，历史剧《鲍里斯·戈都诺夫》，以及《叶甫盖尼·奥涅金》前六章。",
)
print(result)
```

```
{'score': 0.9766427278518677, 'start': 0, 'end': 3, 'answer': '普希金'}
```

即: 这个 pipeline 通过从提供的上下文中提取信息来工作, 它不会凭空生成答案

## summarization (提取摘要)

将文本缩减为较短文本的任务, 同时保留文本中所有(或大部分)主要(重要)的信息

```python
summarizer = pipeline("summarization")
result = summarizer(
    """
    America has changed dramatically during recent years. Not only has the number of
    graduates in traditional engineering disciplines such as mechanical, civil,
    electrical, chemical, and aeronautical engineering declined, but in most of
    the premier American universities engineering curricula now concentrate on
    and encourage largely the study of engineering science. As a result, there
    are declining offerings in engineering subjects dealing with infrastructure,
    the environment, and related issues, and greater concentration on high
    technology subjects, largely supporting increasingly complex scientific
    developments. While the latter is important, it should not be at the expense
    of more traditional engineering.

    Rapidly developing economies such as China and India, as well as other
    industrial countries in Europe and Asia, continue to encourage and advance
    the teaching of engineering. Both China and India, respectively, graduate
    six and eight times as many traditional engineers as does the United States.
    Other industrial countries at minimum maintain their output, while America
    suffers an increasingly serious decline in the number of engineering graduates
    and a lack of well-educated engineers.
"""
)
print(result)
```

```
[{'summary_text': ' America has changed dramatically during recent years . The '
                  'number of engineering graduates in the U.S. has declined in '
                  'traditional engineering disciplines such as mechanical, civil '
                  ', electrical, chemical, and aeronautical engineering . Rapidly '
                  'developing economies such as China and India, as well as other '
                  'industrial countries in Europe and Asia, continue to encourage '
                  'and advance engineering .'}]
```

与生成文本一样, 也可以执行 `max_length`,`min_length` 这一类的参数

## translation (翻译)

```python
# translator = pipeline("translation", model="Helsinki-NLP/opus-mt-fr-en")
# result = translator("Ce cours est produit par Hugging Face.")
# print(result)
```

```
[{'translation_text': 'This course is produced by Hugging Face.'}]
```

与文本生成和摘要一样, 你可以指定结果的 `max_length` 或 `min_length`

## eature-extraction (获取文本的向量表示)