# 处理多个序列

之前的例子中我们逐步拆分了 pipeline 的运行过程, 下面的代码是将拆分的过程组合起来

```python
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification

# 加载 tokenizer 和 model
checkpoint = "distilbert-base-uncased-finetuned-sst-2-english"
tokenizer = AutoTokenizer.from_pretrained(checkpoint)
model = AutoModelForSequenceClassification.from_pretrained(checkpoint)

# 定义要输入的文本
sequence = "I've been waiting for a HuggingFace course my whole life."

# 分词, 得到 tokens
tokens = tokenizer.tokenize(sequence)
# 将 tokens 转换成 id
ids = tokenizer.convert_tokens_to_ids(tokens)
# 将 id 转换成张量
input_ids = torch.tensor(ids)

# 将张量给到模型
model(input_ids)        # 这一行会报错

```

以上代码运行会报错, 原因是我们向模型发送了`一个单独的句子`, 而 `transformers` 模型默认情况下需要一个`句子列表`.

```python
tokenized_inputs = tokenizer(sequence, return_tensors="pt")
print(tokenized_inputs["input_ids"])
```

```
tensor([[  101,  1045,  1005,  2310,  2042,  3403,  2005,  1037, 17662, 12172,
          2607,  2026,  2878,  2166,  1012,   102]])
```

上述代码我们可以看到, 实际上 `tokenizer` 不仅仅是将 `inputs ID` 的列表转换成了`张量`,它还在其上添加了一个维度(
将其放到了一个二维数组中)

以下是修改后的代码

```python
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification

# 加载 tokenizer 和 model
checkpoint = "distilbert-base-uncased-finetuned-sst-2-english"
tokenizer = AutoTokenizer.from_pretrained(checkpoint)
model = AutoModelForSequenceClassification.from_pretrained(checkpoint)

# 定义要输入的文本
sequence = "I've been waiting for a HuggingFace course my whole life."

# 分词, 得到 tokens
tokens = tokenizer.tokenize(sequence)
# 将 tokens 转换成 id
ids = tokenizer.convert_tokens_to_ids(tokens)
# 将 id 转换成张量
input_ids = torch.tensor([ids]) # 添加一个维度
print(input_ids)

# 将张量给到模型
output = model(input_ids)
print(output)   
```

```
tensor([[ 1045,  1005,  2310,  2042,  3403,  2005,  1037, 17662, 12172,  2607,
          2026,  2878,  2166,  1012]])
SequenceClassifierOutput(loss=None, logits=tensor([[-2.7276,  2.8789]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
```

`批处理(Batching)`是一次性通过模型发送多个句子的行为

## 填充输入(Padding)

如果同时输入了两个句子, 他们的长度不一致, 转换为 `inputs ID` 之后的两个列表的长度也不一致,
那么这两个 `inputs ID` 组合成的二维数组不能被转换成张量:

```
batched_ids = [
    [200, 200, 200],
    [200, 200]
]
```

`上面的这个列表(`inputs ID`)不能被转换为张量`

为了解决这个问题, 必须使用填充, 使最终的张量为一个标准的矩形, 填充的过程是通过在值比较少的句子中填充一个名为
`padding_id` 的特殊单词来确保我们所有的句子长度相同.
例如: 如果有 10 个包含 10 个单词的句子和 1 个包含 20 个单词的句子, 填充能确保所有的句子都包含 20 个单词.

以上例子填充后的 `inputs ID` 的二维数组如下:

```
padding_id = 100

batched_ids = [
    [200, 200, 200],
    [200, 200, padding_id],
]
```

以下是一个对 `inputs ID` 进行填充的例子

```python
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification

checkpoint = "distilbert-base-uncased-finetuned-sst-2-english"
tokenizer = AutoTokenizer.from_pretrained(checkpoint)
model = AutoModelForSequenceClassification.from_pretrained(checkpoint)

sequence = "I've been waiting for a HuggingFace course my whole life."
tokens = tokenizer.tokenize(sequence)
ids = tokenizer.convert_tokens_to_ids(tokens)
print(ids)

# 将上面那段话减去最后一个句号
sequence1 = "I've been waiting for a HuggingFace course my whole life"
tokens1 = tokenizer.tokenize(sequence1)
ids1 = tokenizer.convert_tokens_to_ids(tokens1)
print(ids1)
ids1.append(tokenizer.pad_token_id) # 对 ids1 进行填充, 因为只少了一个句号, 所以只需要填充一个位置即可, 如果不进行填充下面张量转换会报错

input_ids = torch.tensor([ids, ids1])
print(input_ids)

output = model(input_ids)
print(output)

sequence2 = "I've been waiting for a HuggingFace course my whole life"
tokens2 = tokenizer.tokenize(sequence2)
ids2 = tokenizer.convert_tokens_to_ids(tokens2)

print(model(torch.tensor([ids])))
print(model(torch.tensor([ids1])))
print(model(torch.tensor([ids2])))
```

```
[1045, 1005, 2310, 2042, 3403, 2005, 1037, 17662, 12172, 2607, 2026, 2878, 2166, 1012]
[1045, 1005, 2310, 2042, 3403, 2005, 1037, 17662, 12172, 2607, 2026, 2878, 2166]
tensor([[ 1045,  1005,  2310,  2042,  3403,  2005,  1037, 17662, 12172,  2607,
          2026,  2878,  2166,  1012],
        [ 1045,  1005,  2310,  2042,  3403,  2005,  1037, 17662, 12172,  2607,
          2026,  2878,  2166,     0]])
SequenceClassifierOutput(loss=None, logits=tensor([[-2.7276,  2.8789],
        [-1.6672,  1.8010]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
SequenceClassifierOutput(loss=None, logits=tensor([[-2.7276,  2.8789]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
SequenceClassifierOutput(loss=None, logits=tensor([[-1.6672,  1.8010]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
SequenceClassifierOutput(loss=None, logits=tensor([[-3.1398,  3.3515]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
```

以上代码可以正常运行, `sequence1` 比 `sequence` 少了一个 `.`, 所以我们给 `ids1` 填充了一位, 这样就可以使 `inputs ID`
的数组成为一个矩形, 以便后面转换成张量.

但是代码最后三行的打印, `ids`和`ids1` 本身内容就不一样(`ids1` 少了最后面的 `.`), 打印出来的结果不一致是正常的, 但 `ids1`
和`ids2` 是一模一样的句子, 模型最终给出的输出也不一样, 造成这样的结果的原因是因为 `ids1` 存在填充, 尽管填充的 `token`
本身没有什么含义, 但是它们的存在会影响模型对句子的理解. 我们需要告诉注意层忽略这些`填充 token`.
这通过注意力`掩码层(attention mask)`来实现.

`注: 填充是针对 inputs ID 这一步来进行的, 而不是对最终的张量进行填充.`

## 注意力掩码(attention mask)层

注意力掩码(attention mask)是与 inputs ID 张量形状完全相同的张量, 用 0 和 1 填充:
1-表示应关注相应的 tokens, 0-表示应忽略相应的 tokens(即: 它们应被模型的注意力层忽视).

以下是一个手动填充注意力掩码的例子:

```python
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification

checkpoint = "distilbert-base-uncased-finetuned-sst-2-english"
tokenizer = AutoTokenizer.from_pretrained(checkpoint)
model = AutoModelForSequenceClassification.from_pretrained(checkpoint)

sequence = "I've been waiting for a HuggingFace course my whole life."

tokens = tokenizer.tokenize(sequence)
ids = tokenizer.convert_tokens_to_ids(tokens)
print(ids)

sequence1 = "I've been waiting for a HuggingFace course my whole life"
tokens1 = tokenizer.tokenize(sequence1)
ids1 = tokenizer.convert_tokens_to_ids(tokens1)
print(ids1)
ids1.append(tokenizer.pad_token_id)

input_ids = torch.tensor([ids, ids1])
print(input_ids)

output = model(input_ids)
print(output)

sequence2 = "I've been waiting for a HuggingFace course my whole life"
tokens2 = tokenizer.tokenize(sequence2)
ids2 = tokenizer.convert_tokens_to_ids(tokens2)

print(model(torch.tensor([ids])))
print(model(torch.tensor([ids1]), attention_mask=torch.tensor([[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0]]))) # 告诉模型, 最后一位是一个填充 token, 忽略它
print(model(torch.tensor([ids2])))
```

```
[1045, 1005, 2310, 2042, 3403, 2005, 1037, 17662, 12172, 2607, 2026, 2878, 2166, 1012]
[1045, 1005, 2310, 2042, 3403, 2005, 1037, 17662, 12172, 2607, 2026, 2878, 2166]
tensor([[ 1045,  1005,  2310,  2042,  3403,  2005,  1037, 17662, 12172,  2607,
          2026,  2878,  2166,  1012],
        [ 1045,  1005,  2310,  2042,  3403,  2005,  1037, 17662, 12172,  2607,
          2026,  2878,  2166,     0]])
SequenceClassifierOutput(loss=None, logits=tensor([[-2.7276,  2.8789],
        [-1.6672,  1.8010]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
SequenceClassifierOutput(loss=None, logits=tensor([[-2.7276,  2.8789]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
SequenceClassifierOutput(loss=None, logits=tensor([[-3.1398,  3.3515]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
SequenceClassifierOutput(loss=None, logits=tensor([[-3.1398,  3.3515]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
```

通过查看比较最后两行的打印结果我们可以看到, 当使用注意力掩码告诉模型最后一位是一个填充 token 之后, 最后两行的输出结果是一致的.

`注: 注意力掩码的作用是告诉模型, 张量中的哪些位置是需要被忽略的. 注意力掩码的形状需要与张量的形状完全一致.`

## 过长句子的处理

对于 `Transformers` 模型, 可以通过模型(输入给模型)的序列长度(单个句子的长度)是有限的.
大多数模型处理多达 `512` 或 `1024` 个的 `tokens` 序列,
当使用模型处理更长的序列时会崩溃. 此问题有如下两种解决方案:

1. 使用支持更长序列长度的模型
2. 截断要输入的序列

不同的模型支持的序列长度各不相同, 有些模型专门用于处理非常长的序列.
也可以通过 max_sequence_length 来截断序列.

### 填充处理

```python
# 将句子序列填充到最长句子的长度
model_inputs = tokenizer(sequences, padding="longest")

# 将句子序列填充到模型的最大长度
# (512 for BERT or DistilBERT)
model_inputs = tokenizer(sequences, padding="max_length")

# 将句子序列填充到指定的最大长度
model_inputs = tokenizer(sequences, padding="max_length", max_length=8)
```

### 截断处理

```python
sequences = ["I've been waiting for a HuggingFace course my whole life.", "So have I!"]

# 将截断比模型最大长度长的句子序列
# (512 for BERT or DistilBERT)
model_inputs = tokenizer(sequences, truncation=True)

# 将截断长于指定最大长度的句子序列
model_inputs = tokenizer(sequences, max_length=8, truncation=True)
```