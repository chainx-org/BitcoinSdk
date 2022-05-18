//
//  ViewController.swift
//  Musig2Demo
//
//  Created by daiwei on 2021/10/16.
//

import UIKit
import Musig2Bitcoin

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.

        // Generate non-threshold signature address
        let PHRASE0 = "flame flock chunk trim modify raise rough client coin busy income smile";
        let private0 = getMyPrivkey(phrase: PHRASE0, pd_passphrase: "")
        let pubkey0 = getMyPubkey(priv: private0)
        let addr0 = getMyAddress(pubkey: pubkey0, network: "signet");
        print("addr0:", addr0)
        let PHRASE1 = "shrug argue supply evolve alarm caught swamp tissue hollow apology youth ethics";
        let private1 = getMyPrivkey(phrase: PHRASE1, pd_passphrase: "")
        let pubkey1 = getMyPubkey(priv: private1)
        let PHRASE2 = "awesome beef hill broccoli strike poem rebel unique turn circle cool system";
        let private2 = getMyPrivkey(phrase: PHRASE2, pd_passphrase: "")
        let pubkey2 = getMyPubkey(priv: private2)

        // Cost of non-threshold signature addresses
        var prev_txs = ["020000000001014be640313b023c3c731b7e89c3f97bebcebf9772ea2f7747e5604f4483a447b601000000000000000002a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bbc027090000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01404dc68b31efc1468f84db7e9716a84c19bbc53c2d252fd1d72fa6469e860a74486b0990332b69718dbcb5acad9d48634d23ee9c215ab15fb16f4732bed1770fdf00000000"];
        var txids: [String] = ["1f8e0f7dfa37b184244d022cdf2bc7b8e0bac8b52143ea786fa3f7bbe049eeae"];
        var input_indexs: [UInt32] = [1];
        var addresses: [String]  = ["tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw", "35516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38", "tb1pexff2s7l58sthpyfrtx500ax234stcnt0gz2lr4kwe0ue95a2e0srxsc68"];
        var amounts: [UInt64] = [100000, 0, 400000];

        var base_tx = generateRawTx(prev_txs: prev_txs, txids: txids, input_indexs:input_indexs, addresses:addresses, amounts: amounts);
        var final_tx = base_tx;
        for i in 0..<txids.count{
            let private_key = "4a84a4601e463bc02dd0b8be03f3721187e9fc3105d5d5e8930ff3c8ca15cf40";
            let sighash = getSighash(tx: base_tx, txid: txids[i],input_index: input_indexs[i], agg_pubkey: "", sigversion: 0);
            print("current sighash:", sighash);
            let schnorr_signature = generateSchnorrSignature(message: sighash, privkey: private_key);
            //let schnorr_signature = "245bb0ea88d95a5976fedfed7bfe36068ab18f7240ab5c6964ca884c352ca19423a56a7ba96e0f24f987551bf58ab248dd1a643293dd463b875e8cbfe2143a2b"
            print("current schnorr_signature:", schnorr_signature);
            final_tx = buildTaprootTx(tx: final_tx, signature: schnorr_signature, txid: txids[i], input_index: input_indexs[i]);
            print("current transaction:", final_tx);
        }
        print("final taproot_tx:", final_tx);

        // Generate threshold signature address
        let threshold_pubkey = generateThresholdPubkey(pubkeys: [pubkey0, pubkey1, pubkey2], threshold: 2);
        let threshold_address = getMyAddress(pubkey: threshold_pubkey, network: "signet");
        print("threshold_address:", threshold_address)

        // Threshold signature address cost
        let private_a = "e5bb018d70c6fb5dd8ad91f6c88fb0e6fdab2c482978c95bb3794ca6e2e50dc2";
        let private_b = "a7150e8f24ab26ebebddd831aeb8f00ecb593df3b80ae1e8b8be01351805f2d6";
        let private_c = "4a84a4601e463bc02dd0b8be03f3721187e9fc3105d5d5e8930ff3c8ca15cf40";
        let pubkey_a = getMyPubkey(priv: private_a);
        let pubkey_b = getMyPubkey(priv: private_b);
        let pubkey_c = getMyPubkey(priv: private_c);

        prev_txs = [ "02000000000101aeee49e0bbf7a36f78ea4321b5c8bae0b8c72bdf2c024d2484b137fa7d0f8e1f01000000000000000003a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bb0000000000000000326a3035516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38801a060000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01409e325889515ed47099fdd7098e6fafdc880b21456d3f368457de923f4229286e34cef68816348a0581ae5885ede248a35ac4b09da61a7b9b90f34c200872d2e300000000"];
        txids = ["8e5d37c768acc4f3e794a10ad27bf0256237c80c22fa67117e3e3e1aec22ea5f"];
        input_indexs = [0];
        addresses = ["tb1pexff2s7l58sthpyfrtx500ax234stcnt0gz2lr4kwe0ue95a2e0srxsc68", "tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw"];
        amounts = [50000, 40000];

        base_tx = generateRawTx(prev_txs: prev_txs, txids: txids, input_indexs: input_indexs, addresses:addresses, amounts: amounts);
        
        // get unsigned tx
        print("unsigned tx:", getUnsignedTx(tx:base_tx))
        
        final_tx = base_tx
        for i in 0..<txids.count{
            let pubkey_bc = getAggPublicKey(pubkeys: [pubkey_b, pubkey_c])
            // compute sighash
            let sighash = getSighash(tx: base_tx, txid: txids[i], input_index: input_indexs[i], agg_pubkey: pubkey_bc, sigversion: 1);
            print("current sighash:", sighash);
            var round1_state0 = getRound1State()
            let state_str = encodeRound1State(state: round1_state0);
            round1_state0 = decodeRound1State(round1_state: state_str)
            let round1_state1 = getRound1State()
            let round1_msg0 = getRound1Msg(state: round1_state0)
            let round1_msg1 = getRound1Msg(state: round1_state1)
            let round2_msg0 = getRound2Msg(state: round1_state0, msg: sighash, priv: private_b, pubkeys: [pubkey_b, pubkey_c], received_round1_msg:[round1_msg1])
            let round2_msg1 = getRound2Msg(state: round1_state1, msg: sighash, priv: private_c, pubkeys: [pubkey_b, pubkey_c], received_round1_msg:[round1_msg0])
            // compute signature
            let multi_signature = getAggSignature(round2_msg: [round2_msg0, round2_msg1])
            //let multi_signature = "2639d4d9882f6e7e42db38dbd2845c87b131737bf557643ef575c49f8fc6928869d9edf5fd61606fb07cced365fdc2c7b637e6ecc85b29906c16d314e7543e94";
            print("current multi_signature:", multi_signature)
            let control_block = generateControlBlock(pubkeys: [pubkey_a, pubkey_b, pubkey_c], threshold: 2, agg_pubkey: pubkey_bc)
            print("current control_block:", control_block)
            // Combination transaction
            final_tx = buildThresholdTx(tx: final_tx, agg_signature: multi_signature, agg_pubkey: pubkey_bc, control: control_block, txid: txids[i], input_index: input_indexs[i]);
            print("current threshold_tx", final_tx);
        }
        print("final threshold_tx:", final_tx);

        // other tool func test
        let script_pubkey = getScriptPubkey(addr: "tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw")
        print("script_pubkey", script_pubkey);
        prev_txs = ["020000000001014be640313b023c3c731b7e89c3f97bebcebf9772ea2f7747e5604f4483a447b601000000000000000002a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bbc027090000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01404dc68b31efc1468f84db7e9716a84c19bbc53c2d252fd1d72fa6469e860a74486b0990332b69718dbcb5acad9d48634d23ee9c215ab15fb16f4732bed1770fdf00000000", "02000000000101aeee49e0bbf7a36f78ea4321b5c8bae0b8c72bdf2c024d2484b137fa7d0f8e1f01000000000000000003a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bb0000000000000000326a3035516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38801a060000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01409e325889515ed47099fdd7098e6fafdc880b21456d3f368457de923f4229286e34cef68816348a0581ae5885ede248a35ac4b09da61a7b9b90f34c200872d2e300000000"];
        input_indexs = [1, 0];
        let spend_outputs = generateSpentOutputs(prev_txs: prev_txs, input_indexs: input_indexs)
        print("spend_outputs", spend_outputs);
    }


}

