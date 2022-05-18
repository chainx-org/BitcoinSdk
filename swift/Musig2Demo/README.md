# **Overview**

This is an api document using musig2 and mast, which helps to build a threshold signature wallet for ios. In order to cope with the taproot upgrade, this api also provides the construction of taproot ordinary transactions and threshold signature transactions.

# **Dependencies**

Step 1. Import project

`File>Add Packages>Github` search [https://github.com/chainx-org/musig2-ios-api](https://github.com/chainx-org/musig2-ios-api). The current version is **2.6.0**.

Step 2. Import and use

```swift
import Musig2Bitcoin
```

# **Api**

## **Construct Transaction**

The following are functions related to constructing transactions

---

### **generateRawTx(txids, input_indexs, addresses, amounts)**

#### illustrate

Construct an original transaction, which is used to calculate the transaction hash and then sign. The entered transaction id and the entered transaction index must correspond one-to-one. The output address and the output quantity must correspond one-to-one. Support **op_return**, just set amout to 0, and the corresponding address setting needs to be accompanied by information.

#### **Parameters and return values**

| **Name**         | **Type** | **Description**                        |
| ---------------- | -------- | -------------------------------------- |
| **prev_txs**     | [String] | List of the original transaction input |
| **txids**        | [String] | List of entered transaction ids        |
| **input_indexs** | [UInt32] | Input transaction index list           |
| **addresses**    | [String] | Output address list                    |
| **amounts**      | [UInt64] | List of output quantities              |
| **Return**       | String   | Original transaction text              |

#### **Return Error**

-`txids and indexes must be equal in length`
-`addresses and amounts must be equal in length`
-`Input count must be greater than 0`
-`Output count must be greater than 0`
-`Invalid Transaction`
-`Invalid Tx Input`
-`Invalid Tx Output`

---

### **getSighash(prev_tx, tx, input_index, agg_pubkey, sigversion)**

#### **illustrate**

Calculate the transaction hash (sighash). A transaction has multiple inputs, and each input needs to calculate a sighash, and then sign the sighash to get the signature.

#### **Parameters and return values**

| **Name**        | **Type** | **Description**                                              |
| --------------- | -------- | ------------------------------------------------------------ |
| **tx**          | String   | Result returned by generateRawTx                             |
| **txid**        | String   | Entered transaction id                                       |
| **input_index** | UInt32   | Input transaction index                                      |
| **agg_pubkey**  | String   | When the input is a non-threshold address, fill in ""; when the input is a threshold address, fill in the aggregate public key (getAggPublicKey) |
| **sigversion**  | UInt32   | When the input is a non-threshold address, fill in 0; when the input is a threshold address, fill in 1; |
| **Return**      | String   | Current input transaction hash                               |

#### **Return Error**

-`Compute Sighash Fail`

---

### **generateSchnorrSignature(message, privkey)**

#### **illustrate**

For non-threshold addresses, use the above sighash and this function to calculate the signature

#### **Parameters and return values**

| **Name**    | **Type** | **Description**                                              |
| ----------- | -------- | ------------------------------------------------------------ |
| **message** | String   | The message to be signed, which is the sighash calculated above |
| **privkey** | String   | Signer's private key                                         |
| **Return**  | String   | Schnorr Signature                                            |

#### **Return Error**

-`Invalid Signature`

---

### **buildTaprootTx(tx, signature, input_index)**

#### **illustrate**

When the address is not threshold, use this function to assemble the signature generated by `generateSchnorrSignature` into the original transaction generated by `generateRawTx`. Each input must be signed once, so multiple inputs must be assembled multiple times.

#### **Parameters and return values**

| **Name**        | **Type** | **Description**                                  |
| --------------- | -------- | ------------------------------------------------ |
| **tx**          | String   | Original transaction calculated by generateRawTx |
| **signature**   | String   | Single Schnorr signature                         |
| **input_index** | UInt32   | Input transaction index                          |
| **Return**      | String   | Return the assembled transaction                 |

#### **Return Error**

-*`Construct Tx Fail`*

---

### **buildThresholdTx(tx, agg_signature, agg_pubkey, control, input_index)**

#### **illustrate**

When the threshold address is used, use this function to assemble the aggregated signature generated by `Musig2` into the original transaction generated by `generateRawTx`. Each input must be signed once, so multiple inputs must be assembled multiple times.

#### **Parameters and return values**

| **Name**          | **Type** | **Description**                                  |
| ----------------- | -------- | ------------------------------------------------ |
| **tx**            | String   | Original transaction calculated by generateRawTx |
| **agg_signature** | String   | Musig2 aggregated signature                      |
| **agg_pubkey**    | String   | Musig2 aggregate public key                      |
| **control**       | String   | Proof generated by Mast                          |
| **input_index**   | UInt32   | Input transaction index                          |
| **Return**        | String   | Return the assembled transaction                 |

#### **Return Error**

-*`Construct Tx Fail`*

---

### **getScriptPubkey(addr)**

#### **illustrate**

Use address to generate scirpt_pubkey, support all address formats.

#### **Parameters and return values**

| **Name**   | **Type** | **Description** |
| ---------- | -------- | --------------- |
| **addr**   | String   | Address         |
| **Return** | String   | scirpt_pubkey   |

#### **Return Error**

-`Invalid Address`

---

### **generateSpentOutputs(prev_txs, input_indexs)**

#### **illustrate**

Generate spend outputs. Use `createTaprootWithdrawTx` in Chainx.

#### **Parameters and return values**

| **Name**         | **Type** | **Description**               |
| ---------------- | -------- | ----------------------------- |
| **prev_txs**     | [String] | Input transaction array       |
| **input_indexs** | [UInt32] | Input transaction index array |
| **Return**       | String   | Serialized spend outputs      |

#### **Return Error**

-*`Invalid Spent Outputs`*

---

### **getUnsignedTx(tx)**

#### **说明**

The unsigned transaction text generated from `generateRawTx`, carrying custom additional information, is not a valid transaction text. The purpose of `getUnsignedTx` is to generate a valid unsigned transaction text that can be parsed by the BTC network.

#### **参数和返回值**

| **Name**   | **Type** | **Description**                                       |
| ---------- | -------- | ----------------------------------------------------- |
| **tx**     | String   | Unsigned transaction text with additional information |
| **Return** | String   | Generate valid unsigned transaction text              |

#### **返回错误**

- `Invalid Transaction`

---

## **Musig2**

The following are functions related to aggregated signatures and aggregated public keys

### **getMyPrivkey(phrase, pd_passphrase)**

#### **illustrate**

Generate private key from mnemonic phrase and password

#### **Parameters and return values**

| **Name**          | **Type** | **Description** |
| ----------------- | -------- | --------------- |
| **phrase**        | String   | mnemonic phrase |
| **pd_passphrase** | String   | Password        |
| **Return**        | String   | Private Key     |

#### **Return Error**

-`Construct Secret Key`

---

### **getMyPubkey(private)**

#### **illustrate**

Generate public key from private key

#### **Parameters and return values**

| **Name**    | **Type** | **Description** |
| ----------- | -------- | --------------- |
| **private** | String   | Private Key     |
| **Return**  | String   | Public Key      |

#### **Return Error**

-*`Null KeyPair Pointer`*
-*`Normal Error`*

---

### getMyAddress**(pubkey, network)**

#### **illustrate**

Generate address

#### **Parameters and return values**

| **Name**    | **Type** | **Description**                                              |
| ----------- | -------- | ------------------------------------------------------------ |
| **pubkey**  | String   | Public Key                                                   |
| **network** | String   | Bitcoin network type, supports "mainnet", "signet", "testnet", "regtest" |
| **Return**  | String   | Address                                                      |

#### **Return Error**

-`Invalid Public Bytes`

---

### **getRound1State()**

#### **illustrate**

Musig2 generates the state of the first round.

#### **Parameters and return values**

| **Name**   | **Type**       | **Description**    |
| ---------- | -------------- | ------------------ |
| **Return** | OpaquePointer? | First round status |

#### **Return Error**

-`null pointer`

---

### **getRound1Msg(state)**

#### **illustrate**

Generate messages through the first round of status for delivery to other participants

#### **Parameters and return values**

| **Name**   | **Type**       | **Description**     |
| ---------- | -------------- | ------------------- |
| **state**  | OpaquePointer? | First round state   |
| **Return** | String         | First round of news |

#### **Return Error**

-`Null Round1 State Pointer`
-*`Normal Error`*

---

### **encodeRound1State(state)**

#### **illustrate**

Serialize the first round of state

#### **Parameters and return values**

| **Name**   | **Type**       | **Description**      |
| ---------- | -------------- | -------------------- |
| **state**  | OpaquePointer? | First round state    |
| **Return** | String         | Serialization result |

#### **Return Error**

-`Null Round1 State Pointer`
-`Encode Fail`

---

### **decodeRound1State(round1_state)**

#### **illustrate**

Deserialize the first round of state

#### **Parameters and return values**

| **Name**         | **Type**       | **Description**                       |
| ---------------- | -------------- | ------------------------------------- |
| **round1_state** | String         | The output value of encodeRound1State |
| **Return**       | OpaquePointer? | First round status                    |

#### **Return Error**

-`null pointer`

---

### **getRound2Msg(state, msg, priv, pubkeys, received_round1_msg)**

#### **illustrate**

Generate the second round of messages

#### **Parameters and return values**

| **Name**                | **Type**       | **Description**                                              |
| ----------------------- | -------------- | ------------------------------------------------------------ |
| **state**               | OpaquePointer? | The output value of encodeRound1State                        |
| **msg**                 | String         | The message to be signed, usually the return value of getSighash |
| **priv**                | String         | Current participant private key                              |
| **pubkeys**             | [String]       | Public keys of all multi-signature participants              |
| **received_round1_msg** | [String]       | The first round of messages received from other multi-signature participants |
| **Return**              | String         | Second round of messages                                     |

#### **Return Error**

-`Invalid Round2 Msg`

---

### **getAggSignature(round2_msg)**

#### **illustrate**

Return the result of the aggregated signature

#### **Parameters and return values**

| **Name**       | **Type** | **Description**                                    |
| -------------- | -------- | -------------------------------------------------- |
| **round2_msg** | String   | The second round of messages from all participants |
| **Return**     | String   | Signature Result                                   |

#### **Return Error**

-`Normal Error`
-`Null Round2 State Pointer`

---

### **getAggPublicKey(pubkeys)**

#### **illustrate**

Generate aggregate public key

#### **Parameters and return values**

| **Name**    | **Type** | **Description**                      |
| ----------- | -------- | ------------------------------------ |
| **pubkeys** | [String] | List of public keys to be aggregated |
| **Return**  | String   | Aggregate Public Key                 |

#### **Return Error**

-`Normal Error`

---

- **Mast**

  The following is the function related to generating the threshold address and proof

  ### generateThresholdAddress**(pubkeys, threshold)**

  #### **illustrate**

  Generate threshold pubke y

  #### **Parameters and return values**

  | **Name**      | **Type** | **Description**         |
  | ------------- | -------- | ----------------------- |
  | **pubkeys**   | [String] | List of all public keys |
  | **threshold** | UInt8    | Threshold               |
  | **Return**    | String   | Aggregate Public Key    |
  
#### **Return Error**

-`Invalid Public Bytes`

---

### **generateControlBlock(pubkeys, threshold, aggPubkey)**

#### **illustrate**

Generate proof

#### **Parameters and return values**

| **Name**      | **Type** | **Description**                                              |
  | ------------- | -------- | ------------------------------------------------------------ |
  | **pubkeys**   | [String] | List of all public keys                                      |
  | **threshold** | UInt8    | Threshold                                                    |
  | **aggPubkey** | String   | The aggregate public key of this multi-signature participant |
  | **Return**    | String   | proof                                                        |

#### **Return Error**

-`Invalid Public Bytes`

---

# **Example**

The following examples provide: constructing a non-threshold address, the cost of a non-threshold address, constructing a threshold signature address, and a threshold signature address cost. The complete code can be viewed in [ViewController.swift](./Musig2Demo/ViewController.swift).

## **Details**

### Generate non-threshold signature address

1. Pass in the mnemonic phrase and password to generate a private key

    ~~~swift
    let private0 = getMyPrivkey(phrase: PHRASE0, pd_passphrase: "")
    ~~~

2. Generate public key

    ~~~swift
    let pubkey0 = getMyPubkey(priv: private0)
    ~~~

3. Generate Address

    ~~~swift
    let addr0 = getMyAddress(pubkey: pubkey0, network: "signet");
    ~~~

### The cost of non-threshold signature addresses

1. **Create an unsigned transaction via `generateRawTx`**. Txids and indexes are used to construct all the inputs of the transaction, and a txid and an index are used to locate the only unspent output. Below prev_txs, txids and input_indexs have the same length and correspond one to one. Addresses and amounts are used to construct all the outputs of the transaction. An adddress and an amount indicate how many coins are sent to an address. There is no order requirement for adddress, just a one-to-one correspondence between amounts. And here `1f8e0f7dfa37b184244d022cdf2bc7b8e0bac8b52143ea786fa3f7bbe049eeae` and `1` uniquely determine an unspent output. The address of this unspent output is a **non-threshold address**. `35516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38` represents `op_return`, and its corresponding amout is 0. `tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw` is the recipient's address and `100000` is the transfer amount. `tb1pexff2s7l58sthpyfrtx500ax234stcnt0gz2lr4kwe0ue95a2e0srxsc68` is the change address, and `400000` is the change amount. Refer to **Calculation of Handling Fee and Change Balance** for the calculation method.

   ~~~swift
   var prev_txs = [ "020000000001014be640313b023c3c731b7e89c3f97bebcebf9772ea2f7747e5604f4483a447b601000000000000000002a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bbc027090000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01404dc68b31efc1468f84db7e9716a84c19bbc53c2d252fd1d72fa6469e860a74486b0990332b69718dbcb5acad9d48634d23ee9c215ab15fb16f4732bed1770fdf00000000"];
   var txids: [String] = ["1f8e0f7dfa37b184244d022cdf2bc7b8e0bac8b52143ea786fa3f7bbe049eeae"];
   var input_indexs: [UInt32] = [1];
   var addresses: [String] = ["tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw", "35516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255"cff464"c6d4d71417
   var amounts: [UInt64] = [100000, 0, 400000];
   var base_tx = generateRawTx(prev_txs: prev_txs, txids: txids, input_indexs:input_indexs, addresses:addresses, amounts: amounts);
   var final_tx = base_tx;
   ~~~

2. **Sign the output to be spent**. To sign the UTXO to be spent, first calculate the sighash of the unspent output. The signature is to sign the sighash.

   txid and input_index are used to locate the output to be spent, agg_pubkey fills in the string `""` for non-threshold signature addresses, sigversion fills in 0 for non-threshold signature addresses, tx is the currently constructed transaction.**Note that when calculating sighash, always use the above `generateRawTx` to construct the result and cannot be changed. **

   ~~~swift
   let sighash = getSighash(tx: base_tx, txid: txids[i],input_index: input_indexs[i], agg_pubkey: "", sigversion: 0);
   ~~~

   After calculating the sighash, use the private key to sign it. The message refers to sighash, and the privkey refers to the private key.

   ~~~swift
   let schnorr_signature = generateSchnorrSignature(message: sighash, privkey: private_key);
   ~~~

3. **Assemble the above signature into the transaction**. tx is the current transaction to be constructed, and txid and input_index are still used to locate the input corresponding to the signature in tx.

   ~~~swift
   final_tx = buildTaprootTx(tx: final_tx, signature: schnorr_signature, txid: txids[i], input_index: input_indexs[i]);
   ~~~

   **Note that if there are multiple inputs in tx, you need to repeat Step2 and Step3 to sign each output and add it to tx, as shown in the following for loop:. **

   ![](https://cdn.jsdelivr.net/gh/AAweidai/PictureBed@master/taproot/16385263088061638526308780.png)

### Generate threshold signature address

1. Generate a 2-of-3 threshold signature address as follows. First, pass in the public keys and thresholds of all participants to generate the threshold public keys.

    ~~~swift
    let threshold_pubkey = generateThresholdPubkey(pubkeys: [pubkey0, pubkey1, pubkey2], threshold: 2);
    ~~~
    
2. Then encode the public key into an address to get the threshold address

    ~~~swift
    let threshold_address = getMyAddress(pubkey: threshold_pubkey, network: "signet");
    ~~~

### Threshold signature address cost

1. **Create an unsigned transaction via `generateRawTx`**. Txids and indexes are used to construct all the inputs of the transaction, and a txid and an index are used to locate the only unspent output. Below prev_txs, txids and input_indexs have the same length and correspond one to one. Addresses and amounts are used to construct all the outputs of the transaction. An adddress and an amount indicate how many coins are sent to an address. There is no order requirement for adddress, just a one-to-one correspondence between amounts. Here `8e5d37c768acc4f3e794a10ad27bf0256237c80c22fa67117e3e3e1aec22ea5f` and `0` uniquely determine an unspent output. Note that the address of this unspent output is a **threshold address**. `tb1pexff2s7l58sthpyfrtx500ax234stcnt0gz2lr4kwe0ue95a2e0srxsc68` is the recipient's address, `50000` is the transfer amount. `tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw` is the change address, and `40000` is the change amount. Of course, you can also bring `op_return` here. The calculation method refers to **Calculation of handling fee and change balance**

   ~~~swift
   prev_txs = [ "02000000000101aeee49e0bbf7a36f78ea4321b5c8bae0b8c72bdf2c024d2484b137fa7d0f8e1f01000000000000000003a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bb0000000000000000326a3035516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38801a060000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01409e325889515ed47099fdd7098e6fafdc880b21456d3f368457de923f4229286e34cef68816348a0581ae5885ede248a35ac4b09da61a7b9b90f34c200872d2e300000000"];
   txids = ["8e5d37c768acc4f3e794a10ad27bf0256237c80c22fa67117e3e3e1aec22ea5f"];
   input_indexs = [0];
   addresses = ["tb1pexff2s7l58sthpyfrtx500ax234stcnt0gz2lr4kwe0ue95a2e0srxsc68", "tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw"];
   amounts = [50000, 40000];
   base_tx = generateRawTx(prev_txs: prev_txs, txids: txids, input_indexs: input_indexs, addresses:addresses, amounts: amounts);
   final_tx = base_tx
   ~~~

2. **Sign the output to be spent**. To sign the UTXO to be spent, first calculate the sighash of the unspent output, and the signature is to sign the sighash.

   The txid and input_index are used to locate the output to be spent. Agg_pubkey fills in the empty string aggregate public key for the threshold signature address. The following is the aggregate public key of B and C for two people, then fill in the aggregate public key of B and C. sigversion fills in 1 for the threshold signature address, and tx is the currently constructed transaction.**Note that when calculating sighash, always use the above `generateRawTx` to construct the result and cannot be changed. **

   **Calculate sighash**

   ~~~swift
   let pubkey_bc = getAggPublicKey(pubkeys: [pubkey_b, pubkey_c])
   let sighash = getSighash(tx: base_tx, txid: txids[i], input_index: input_indexs[i], agg_pubkey: pubkey_bc, sigversion: 1);
   ~~~

   **Calculate signature**: After calculating sighash, B and C use Musig2 to aggregate signatures. The signed message is sighash.

   ~~~swift
   var round1_state0 = getRound1State()
   let state_str = encodeRound1State(state: round1_state0);
   round1_state0 = decodeRound1State(round1_state: state_str)
   let round1_state1 = getRound1State()
   let round1_msg0 = getRound1Msg(state: round1_state0)
   let round1_msg1 = getRound1Msg(state: round1_state1)
   let round2_msg0 = getRound2Msg(state: round1_state0, msg: sighash, priv: private_b, pubkeys: [pubkey_b, pubkey_c], received_round1_msg:[round1_msg1])
   let round2_msg1 = getRound2Msg(state: round1_state1, msg: sighash, priv: private_c, pubkeys: [pubkey_b, pubkey_c], received_round1_msg:[round1_msg0])
   let multi_signature = getAggSignature(round2_msg: [round2_msg0, round2_msg1])
   ~~~

   ​	The following is a detailed introduction to the above-mentioned Musig2 multi-signature process, divided into the following steps:

   1. Generate the state of the first round

      ~~~swift
      var round1_state0 = getRound1State()
      ~~~

   2. Obtain the first round of messages through the first round of status and pass them to other signing participants.

      ~~~swift
      let round1_msg0 = getRound1Msg(state: round1_state0)
      ~~~

   3. Get the first round of messages from other signing participants, generate the second round of messages, and pass them to other participants. `received_round1_msg` is the first round of messages received from other participants. `pubkeys` are the public keys of all participants. `msg` is the message to be signed. `state` is the state of the first round. `priv` is the signer's private key.

      ~~~swift
      let round2_msg0 = getRound2Msg(state: round1_state0, msg: sighash, priv: private_b, pubkeys: [pubkey_b, pubkey_c], received_round1_msg:[round1_msg1])
      ~~~

   4. Use the second round of messages from all participants to generate aggregate signatures. `round2_msg` is the second round of messages from all participants.

      ~~~swift
      let multi_signature = getAggSignature(round2_msg: [round2_msg0, round2_msg1])
      ~~~

   **Calculate proof**: The cost of threshold signature not only requires signature, but also calculates proof. It is necessary to pass in the public key of everyone, the threshold, and the aggregate public key of participants B and C of this signature.

   ~~~swift
   let control_block = generateControlBlock(pubkeys: [pubkey_a, pubkey_b, pubkey_c], threshold: 2, agg_pubkey: pubkey_bc)
   ~~~

3. **Assemble the above signature and proof for transaction**. tx is the current transaction to be constructed, agg_signature is the aggregate signature of B and C, agg_pubkey is the aggregate public key of B and C, txid and input_index are still used to locate the input corresponding to the signature in tx, and the unspent output corresponding to txid and input_index The second step is corresponding.

   ~~~swift
   final_tx = buildThresholdTx(tx: final_tx, agg_signature: multi_signature, agg_pubkey: pubkey_bc, control: control_block, txid: txids[i], input_index: input_indexs[i]);
   ~~~

   **Note that if there are multiple inputs in tx, you need to repeat Step2 and Step3 to sign each output and add it to tx, as shown in the following for loop:**

   ![](https://cdn.jsdelivr.net/gh/AAweidai/PictureBed@master/taproot/16385266017641638526601743.png)

## Calculation of handling fee and change balance

Background: A wants to transfer money to `B 2BTC`, `C 3BTC`

1. Find all unspent transaction txids and balances through the address of A, and sort them from largest to smallest, assuming it is `[(txid1, 4), (txid2, 2), (tixd3, 1), (tixd4, 1) ]`.

2. Accumulate the txids and balance list and find the txid that is greater than the output amount 2+3=5, that is, txid2. If it is not found, it will return that the transfer is not allowed.

3. Extend one bit from txid2 backward, using `[(txid1, 4), (txid2, 2), (tixd3, 1)]` as input. If txid2 is the last one, use `[(txid1, 4), (txid2, 2)]` as input.

4. Use the number of inputs and outputs and the following formula to estimate the number of transaction bytes:

   **Estimation of the number of bytes spent by non-threshold addresses**

   ~~~
   105 + 58 * input_count(threshold_address) + 43 * output_count
   ~~~
   `input_count(taproot_address)` represents the number of input txid when the non-threshold address is spent

   **Estimation of the number of bytes of the threshold address**

   ```
   105 + 141 * input_count(threshold_address) + 43 * output_count
   ```
   `input_count(threshold_address)` represents the number of input txid when the threshold address is spent

5. Multiply the number of bytes by the current `FEE RATES` to get the transaction fee.

6. Enter `total amount-(total amount of output + handling fee)` to get `change amount`. If it is negative, there is no change (that is, the change address and amount are not filled in the output list), and the transaction fee becomes `Total input amount-Total amount to output`.

# Other

- Convert the address into the locked script script_pubkey output in the Bitcoin transaction

   ~~~swift
   let script_pubkey = getScriptPubkey(addr: "tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw")
   ~~~

- Pass in a set of transactions and a corresponding set of indexes to locate a set of outputs to be spent

   ~~~swift
   let spend_outputs = generateSpentOutputs(prev_txs: prev_txs, input_indexs: input_indexs)
   ~~~

- 从`generateRawTx`生成的未签名的交易原文中提取有效的未签名的交易原文。

  ~~~swift
  print("unsigned tx:", getUnsignedTx(tx:base_tx))
  ~~~

  
