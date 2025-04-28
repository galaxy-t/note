# Tokenizer

`tokenizer` 是 `pipeline` 的核心组件之一, 它有一个明确的目标: 将文本转换为模型可以处理的数据.
模型只能处理数字, 因此 `tokenizer` 需要将我们的文本输入转换为数字.

上述所说我们可以称之为`编码能力`, 但实际上 `tokenizer` 不仅提供了`编码能力`, 还提供了`解码能力`,
它还可以把模型的输出再解码成我们可以阅读的文本.

## 加载

```python
from transformers import BertTokenizer, AutoTokenizer

tokenizer1 = BertTokenizer.from_pretrained("bert-base-cased")
tokenizer2 = AutoTokenizer.from_pretrained("bert-base-cased")

tokens = tokenizer2("Using a Transformer network is simple")
print(tokens)
```

```
{
    'input_ids': [101, 7993, 170, 13809, 23763, 2443, 1110, 3014, 102], 
    'token_type_ids': [0, 0, 0, 0, 0, 0, 0, 0, 0], 
    'attention_mask': [1, 1, 1, 1, 1, 1, 1, 1, 1]
}
```

以上代码, 我们可以直接根据 `checkpoint` 加载一个模型的 `tokenizer`, 上面运行的结果中 `input_ids` 实际上就是对输入编码后的结果

## 编码(encoding)

将文本翻译成数字的过程称之为`编码(encoding)`, 编码可以分为以下两个步骤:

##### 1. 分词

得到 `tokens`, 不同的分词器算法不同, 所以需要使用模型预训练时相同的算法来进行分词

```python
from transformers import AutoTokenizer

tokenizer = AutoTokenizer.from_pretrained("bert-base-cased")

sequence = "Using a Transformer network is simple"
tokens = tokenizer.tokenize(sequence)

print(tokens)
```

```
['Using', 'a', 'Trans', '##former', 'network', 'is', 'simple']
```

以上代码演示了如何对一个句子进行分词

##### 2. 将每一个词转换成 `inputs ID`,

将 `tokens` 转换为数字(以便我们可以用它们构建一个张量并将它们提供给模型), `tokenizer` 有一个 `词汇表(vocabulary)`, 同样,
我们需要使用与模型预训练时相同的词汇表.

```python
ids = tokenizer2.convert_tokens_to_ids(tokens)
print(ids)
```

```
[7993, 170, 13809, 23763, 2443, 1110, 3014]
```

上述代码的作用是将 `tokens` 转换为 `inputs ID`

## 解码(Decoding)

与编码整好相反, `解码(Decoding)`是将 `inputs ID` 转换成一个字符串

```python
decoded_strings = tokenizer2.decode(ids)
print(decoded_strings)
```

```
Using a Transformer network is simple
```

以上代码, `decode` 方法不仅将 `inputs ID` 转换成 `token`, 还将属于相同单词的 `tokens` 组合在一起生成可读的句子.

