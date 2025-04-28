# 模型(model)

之前我们使用 `AutoModel` 类从 `checkpoint` 实例化一些模型, `AutoModel` 类及其所有的相关类(如:
`AutoModelForSequenceClassification` 等)其实就是对库中可用的各种模型的简单包装. 它是一个智能的包装, 因为它可以自动猜测你的
`checkpoint` 适合的模型架构, 然后实例化一个具有相同架构的模型.

但如果你知道要使用的模型的类型(`BERT`, `GPT` 等), 你可以直接使用其架构相对应的模型类.

## 创建 Transformers 模型

以创建一个 `BERT` 模型为例, 以下是一段简单的代码

```python
from transformers import BertConfig, BertModel

# 初始化 Config 类
config = BertConfig()
print(config)

# 从 Config 类初始化模型
model = BertModel(config)
```

```
BertConfig {
  "attention_probs_dropout_prob": 0.1,
  "classifier_dropout": null,
  "hidden_act": "gelu",
  "hidden_dropout_prob": 0.1,
  "hidden_size": 768,
  "initializer_range": 0.02,
  "intermediate_size": 3072,
  "layer_norm_eps": 1e-12,
  "max_position_embeddings": 512,
  "model_type": "bert",
  "num_attention_heads": 12,
  "num_hidden_layers": 12,
  "pad_token_id": 0,
  "position_embedding_type": "absolute",
  "transformers_version": "4.51.1",
  "type_vocab_size": 2,
  "use_cache": true,
  "vocab_size": 30522
}
```

上面 `config` 中包含了很多属性, 其中一些看起来比较熟悉的如: `hidden_size` 属性定义了 `hidden_states`(隐状态)向量的大小,
而 `num_hidden_layers` 定义了 `Transformer 模型` 的层数.

上面实例化的这个模型是可以运行并得到结果的, 但它的输出会胡言乱语, 它需要先进行训练才能正常使用. 当然,
对一个模型进行预训练是一件非常耗时且巨大的工作, 所以我们可以加载已经训练好的模型.

以下是例子用来加载一个 BERT 的预训练模型

```python
from transformers import BertModel

model = BertModel.from_pretrained("bert-base-cased")
print(model)
```

以上代码, 使用 `BertModel` 来等效的代替 `AutoModel`.

如果你的代码适用于一个 `checkpoint` 那么它就可以在另一个 `checkpoint` 无缝地工作. 即使体系结构不同, 这也适用, 只要
`checkpoint`
是针对同类的任务(例如: 情绪分析任务)训练的.

## 保存模型

```python
model.save_pretrained("C://dev/model/bert-base-cased")
```

使用上述代码可以将模型保存到指定的文件夹, 打开文件夹会发现两个文件 `config.json`和`model.safetensors`, 其中
`config.json` 这个文件包含了 构建模型架构所需的属性. `model.safetensors` 文件被称为 `state dictionary(状态字典)`,
它包含了模型的所有权重. 这两个文件是相辅相成的, 配置文件是构建模型架构(被 Transformers 调用?)所必须的, 而模型权重就是模型的参数.