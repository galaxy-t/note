# 管道的内部

    使用 model和tokenizer 来复刻上一步的 pipeline() 函数

pipeline 集成了三个步骤: `预处理`,`模型计算`和`后处理`

## tokenizer

`transformers` 模型无法直接处理原始文本, 因此管道第一步是将输入的文本转换为模型能够理解的数字.

`pipeline` 使用 `tokenizer` 进行预处理. 其处理过程如下:

1. 将输入拆分为 单词,子单词,符号(如标点符号), 称为 `token`
2. 将每个 `token` 映射到一个数字, 称为 `inputs ID`
3. 添加模型需要的其它输入, 例如特殊标记(如: [CLS]和[SEP]), 这个结果称为 `tensor(张量)`
   > 位置编码: 指示每个 `token` 在句子中的位置
   >
   > 段落标记: 区分不同段落的文本
   >
   > 特殊标记: 例如 [CLS]和[SEP] 标记, 用于标识句子的开头和结尾

`注: 在使用模型时, 所有这些预处理都需要与模型训练时的方式完全相同.`

以下是实例化一个 `tokenizer` 对象的例子:

```python
from transformers import AutoTokenizer

checkpoint = "distilbert-base-uncased-finetuned-sst-2-english"
tokenizer = AutoTokenizer.from_pretrained(checkpoint)

strs = [
    "I've been waiting for a HuggingFace course my whole life.",
    "I hate this so much!",
]

inputs = tokenizer(strs, padding=True, truncation=True, return_tensors="pt")
print(inputs)
```

`其中 checkpoint 为对应模型的名称, 一般 model和model的tokenizer 的名称是一致的.`

以上例子返回的结果如下:

```
{
    'input_ids': tensor([
        [  101,  1045,  1005,  2310,  2042,  3403,  2005,  1037, 17662, 12172, 2607,  2026,  2878,  2166,  1012,   102],
        [  101,  1045,  5223,  2023,  2061,  2172,   999,   102,     0,     0,     0,     0,     0,     0,     0,     0]
    ]), 
    'attention_mask': tensor([
        [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
        [1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0]
    ])
}
```

以上结果被称为 `tensor(张量)`, `return_tensors="pt"` 这个参数用来控制返回的 `tensor` 类型, (`PyTorch`,`TensorFlow` 或纯
`NumPy`)

`PyTorch` 张量的结果中包含两个键, `input_ids` 和 `attention_mask`, `input_ids ` 包含两行整数(每个输入的句子一行)

而如果没有 `padding=True`,`truncation=True`,`return_tensors="pt"` 这三个参数, 那么返回是这样的:

```
{
   'input_ids': [101, 7592, 1010, 2026, 2171, 2003, 3533, 102], 
   'attention_mask': [1, 1, 1, 1, 1, 1, 1, 1]
}
```

上面这个输出被称为 `inputs ID`

以上使用 `padding=True`,`truncation=True`,`return_tensors="pt"` 这三个参数来控制返回值的过程, 被称为 `inputs ID` 转换为
`tensor` 的过程.

## model

以下是实例化一个 `model` 对象的例子:

```python
from transformers import AutoModel

checkpoint = "distilbert-base-uncased-finetuned-sst-2-english"
model = AutoModel.from_pretrained(checkpoint)
```

上面的例子中, 使用 `AutoModel.from_pretrained(checkpoint)` 将 `distilbert-base-uncased-finetuned-sst-2-english`
这个模型下载下来(实际已经被缓存下来了, 之前用到过), 并用它实例化了一个模型.

如果我们将上面 `tokenizer` 的返回值给到这个模型, 代码如下:

```python
outputs = model(**inputs)
print(outputs)
print(outputs.last_hidden_state.shape)
```

那么其打印结果如下:

```
BaseModelOutput(last_hidden_state=tensor([[[-0.1798,  0.2333,  0.6321,  ..., -0.3017,  0.5008,  0.1481],
         [ 0.2758,  0.6497,  0.3200,  ..., -0.0760,  0.5136,  0.1329],
         [ 0.9046,  0.0985,  0.2950,  ...,  0.3352, -0.1407, -0.6464],
         ...,
         [ 0.1466,  0.5661,  0.3235,  ..., -0.3376,  0.5100, -0.0561],
         [ 0.7500,  0.0487,  0.1738,  ...,  0.4684,  0.0030, -0.6084],
         [ 0.0519,  0.3729,  0.5223,  ...,  0.3584,  0.6500, -0.3883]],

        [[-0.2937,  0.7283, -0.1497,  ..., -0.1187, -1.0227, -0.0422],
         [-0.2206,  0.9384, -0.0951,  ..., -0.3643, -0.6605,  0.2407],
         [-0.1536,  0.8988, -0.0728,  ..., -0.2189, -0.8528,  0.0710],
         ...,
         [-0.3017,  0.9002, -0.0200,  ..., -0.1082, -0.8412, -0.0861],
         [-0.3338,  0.9674, -0.0729,  ..., -0.1952, -0.8181, -0.0634],
         [-0.3454,  0.8824, -0.0426,  ..., -0.0993, -0.8329, -0.1065]]],
       grad_fn=<NativeLayerNormBackward0>), hidden_states=None, attentions=None)
       
torch.Size([2, 16, 768])
```

### hidden states(隐状态)

以上是模型的整个输出(`outputs`), `outputs` 是一个特殊的对象(继承自 `ModelOutput`)类.

其中, `outputs.last_hidden_state` 被称为 `hidden states(隐状态)`, 也被成为特征. 这代表模型对输入的上下文理解. 这是一个
`高纬向量`(所谓的 `高纬向量` 可以理解为一个多维数组)

而通过打印 `outputs.last_hidden_state` 的形状我们可以看出这个 `隐状态` 有三个维度:

1. Batch size(批次大小): 一次处理的序列数(在我们的示例中为 2), 实际上就是我们一共输入了几句话
2. Sequence length(序列长度): 表示序列(句子)的长度(在我们的示例中为 16), 实际上是 `inputs ID` 列表的长度(最长的那个)
3. Hidden size(隐藏层大小): 每个模型输入的向量维度

`最后一个 隐藏层 的纬度可能非常大(对于较小的模型, 常见的是 768, 对于较大的模型, 这个数字可以达到 3072 或更多)`

### 模型头(head)

一个模型(此处指具体的整个模型文件或者文件夹)其中包含的能力实际上是有多个的

1. Transformer 主体(编码层/解码层), 这是 ChatGPT 的说法, 官网说的是 嵌入层+后续层(如果存在后续层). 这一块负责对输入进行语义理解和上下文建模,
   输出的结果就是隐状态
2. 模型头(任务层), 负责具体的特定任务(如: 分类,NER,QA 等), 输出最终的任务结果(这个任务结果不能再被称为隐状态!!!)

`注: 此时可以看出, 所谓的隐状态就是 编码层/解码层 的输出, 这个输出又可以作为 模型头 的输入`

而 `Transformers` 提供的 `AutoModel` 这个类, 仅仅只能调用模型的 `编码层/解码层` 的能力(官网的说法叫 `隐状态检索`),
想要调用到具体 `模型头` 的能力, 则还需要根据模型来使用对应的类, 如下:

AutoModelForSequenceClassification(文本分类)
AutoModelForTokenClassification(命名实体识别)
AutoModelForCausalLM(生成(GPT))
AutoModelForQuestionAnswering(问答系统)
...

以情感分类为例, 我们需要一个带有序列分类头的模型(能够将句子分类为积极或消极).
因此, 我们不选用 `AutoModel` 类, 而是使用 `AutoModelForSequenceClassification`.
也就是说前面写的 `model = AutoModel.from_pretrained(checkpoint)` 并不能得到情感分类任务的结果, 因为没有加载
`Model head`.
`AutoModelForSequenceClassification` 类在 `AutoModel` 的基础上添加了一个序列分类头部, 可以将文本分类为不同的类别.
代码如下:

```python
from transformers import AutoModelForSequenceClassification

checkpoint = "distilbert-base-uncased-finetuned-sst-2-english"
model = AutoModelForSequenceClassification.from_pretrained(checkpoint)
outputs = model(**inputs)
print(outputs)
print(outputs.logits.shape)
```

```
SequenceClassifierOutput(loss=None, logits=tensor([[-1.5607,  1.6123],
        [ 4.1692, -3.3464]], grad_fn=<AddmmBackward0>), hidden_states=None, attentions=None)
torch.Size([2, 2])
```

从上面的输出我们可以看到其比隐状态少了很多东西, 即: 向量的尺寸和纬度都小了很多. 这是因为模型头将隐状态向量中的信息压缩成了两个值.

## 对输出进行后续的处理

模型输出的值本身不一定有意义, 如:

```python
print(outputs.logits)
```

```
tensor([[-1.5607,  1.6123],
        [ 4.1692, -3.3464]], grad_fn=<AddmmBackward>)
```

上面可以看到模型预测的第一句和第二句的结果, 这些并不是概率(之前的例子可以看到情感分析的返回值大概是:
`['NEGATIVE': 0.9, 'POSITIVE': 0.1]`, 里面显示了这个句子是 积极/消极 的概率), 但 `logits(对数几率)`
是模型最后一层输出的原始的,未标准化的分数. 要转化为概率, 它们需要经过 `SoftMax` 层（所有 `Transformers` 模型的输出都是
`logits`, 因为训练时的损失函数通常会将最后的激活函数(如 `SoftMax`)与实际的损失函数(如交叉熵)融合)

`注: 我他妈也理解不了 SoftMax 的具体执行, 但是可以明确一点的是, SoftMax 并不是模型提供的具体能力, 而是一个数学运算公式, 它由 PyTorch 提供, 它的作用是把一组任意实数(logits)编程一组概率值, 总和为 1, 用于表示每个分类的可能性(百分比).`

以下是具体实现:

```python
import torch

predictions = torch.nn.functional.softmax(outputs.logits, dim=-1)
print(predictions)
```

```
tensor([[4.0195e-02, 9.5980e-01],
        [9.9946e-01, 5.4418e-04]], grad_fn=<SoftmaxBackward>)
```

从上面的结果可以看出, 模型预测第一句的输出是 [0.0402, 0.9598] ，第二句 [0.9995, 0.0005]. 这些是可直接使用的概率分数.
`注: 卧槽, 4.0195e-02 就是小数点左移两位, 9.5980e-01 就是小数点左移一位? 开眼了`

如果想知道数组里的每个下标对应的含义, 可以使用下面的代码打印出来

```python
print(model.config.id2label)
```

```
{0: 'NEGATIVE', 1: 'POSITIVE'}
```

即: 下标为 0 的那个数代表 `NEGATIVE`, 下标为 1 的那个数代表 `POSITIVE`







