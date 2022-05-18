package com.chainx.bitcoindemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.chainx.musig2bitcoin.Mast;
import com.chainx.musig2bitcoin.Musig2;
import com.chainx.musig2bitcoin.Transaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Generate non-threshold signature address
        String PHRASE0 = "flame flock chunk trim modify raise rough client coin busy income smile";
        String private0 = Musig2.getMyPrivkey(PHRASE0, "");
        String pubkey0 = Musig2.getMyPubkey(private0);
        String addr0 = Transaction.getMyAddress(pubkey0, "signet");
        System.out.println("addr0:" + addr0);
        String PHRASE1 = "shrug argue supply evolve alarm caught swamp tissue hollow apology youth ethics";
        String private1 = Musig2.getMyPrivkey(PHRASE1, "");
        String pubkey1 = Musig2.getMyPubkey(private1);
        String PHRASE2 = "awesome beef hill broccoli strike poem rebel unique turn circle cool system";
        String private2 = Musig2.getMyPrivkey(PHRASE2, "");
        String pubkey2 = Musig2.getMyPubkey(private2);

        // Cost of non-threshold signature addresses
        String[] prev_txs = new String[]{"020000000001014be640313b023c3c731b7e89c3f97bebcebf9772ea2f7747e5604f4483a447b601000000000000000002a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bbc027090000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01404dc68b31efc1468f84db7e9716a84c19bbc53c2d252fd1d72fa6469e860a74486b0990332b69718dbcb5acad9d48634d23ee9c215ab15fb16f4732bed1770fdf00000000"};
        String[] txids = new String[]{"1f8e0f7dfa37b184244d022cdf2bc7b8e0bac8b52143ea786fa3f7bbe049eeae"};
        long[] input_indexs = new long[]{1};
        String[] addresses  = new String[]{"tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw", "35516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38", "tb1pexff2s7l58sthpyfrtx500ax234stcnt0gz2lr4kwe0ue95a2e0srxsc68"};
        long[] amounts = new long[]{100000, 0, 400000};
        String base_tx = Transaction.generateRawTx(prev_txs, txids, input_indexs, addresses, amounts);
        String unsigned_tx = Transaction.getUnsignedTx(base_tx);
        System.out.println("unsigned tx: " + unsigned_tx);
        String final_tx = base_tx;
        for (int i = 0; i < txids.length; i++) {
            String private_key = "4a84a4601e463bc02dd0b8be03f3721187e9fc3105d5d5e8930ff3c8ca15cf40";
            String sighash = Transaction.getSighash(base_tx, txids[i], input_indexs[i], "", 0);
            System.out.println("current sighash:" + sighash);
            String schnorr_signature = Transaction.generateSchnorrSignature(sighash, private_key);
            //schnorr_signature = "245bb0ea88d95a5976fedfed7bfe36068ab18f7240ab5c6964ca884c352ca19423a56a7ba96e0f24f987551bf58ab248dd1a643293dd463b875e8cbfe2143a2b"
            System.out.println("current schnorr_signature:" + schnorr_signature);
            final_tx = Transaction.buildTaprootTx(base_tx, schnorr_signature, txids[i], input_indexs[i]);
            System.out.println("current taproot_tx:" + final_tx);
        }
        System.out.println("final threshold_tx:" + final_tx);

        // Generate threshold signature address
        String threshold_pubkey = Mast.generateThresholdPubkey(new String[]{pubkey0, pubkey1, pubkey2}, (byte) 2);
        String threshold_address = Transaction.getMyAddress(threshold_pubkey, "signet");
        System.out.println("threshold_address:" + threshold_address);

        // Threshold signature address cost
        String private_a = "e5bb018d70c6fb5dd8ad91f6c88fb0e6fdab2c482978c95bb3794ca6e2e50dc2";
        String private_b = "a7150e8f24ab26ebebddd831aeb8f00ecb593df3b80ae1e8b8be01351805f2d6";
        String private_c = "4a84a4601e463bc02dd0b8be03f3721187e9fc3105d5d5e8930ff3c8ca15cf40";
        String pubkey_a = Musig2.getMyPubkey(private_a);
        String pubkey_b = Musig2.getMyPubkey(private_b);
        String pubkey_c = Musig2.getMyPubkey(private_c);

        prev_txs = new String[]{"02000000000101aeee49e0bbf7a36f78ea4321b5c8bae0b8c72bdf2c024d2484b137fa7d0f8e1f01000000000000000003a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bb0000000000000000326a3035516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38801a060000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01409e325889515ed47099fdd7098e6fafdc880b21456d3f368457de923f4229286e34cef68816348a0581ae5885ede248a35ac4b09da61a7b9b90f34c200872d2e300000000"};
        txids = new String[]{"8e5d37c768acc4f3e794a10ad27bf0256237c80c22fa67117e3e3e1aec22ea5f"};
        input_indexs = new long[]{0};
        addresses = new String[]{"tb1pexff2s7l58sthpyfrtx500ax234stcnt0gz2lr4kwe0ue95a2e0srxsc68", "tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw"};
        amounts = new long[]{50000, 40000};
        base_tx = Transaction.generateRawTx(prev_txs, txids, input_indexs, addresses, amounts);
        unsigned_tx = Transaction.getUnsignedTx(base_tx);
        System.out.println("unsigned tx: " + unsigned_tx);
        final_tx = base_tx;
        for (int i = 0; i < txids.length; i++) {
            String pubkey_bc = Musig2.getAggPublicKey(new String[]{pubkey_b, pubkey_c});
            String sighash = Transaction.getSighash(base_tx, txids[i], input_indexs[i], pubkey_bc, 1);
            System.out.println("current sighash:" + sighash);
            long round1_state0 = Musig2.getRound1State();
            String round1_state_str = Musig2.encodeRound1State(round1_state0);
            round1_state0 = Musig2.decodeRound1State(round1_state_str);
            long round1_state1 = Musig2.getRound1State();
            String round1_msg0 = Musig2.getRound1Msg(round1_state0);
            String round1_msg1 = Musig2.getRound1Msg(round1_state1);
            String round2_msg0 = Musig2.getRound2Msg(round1_state0, sighash, private_b, new String[]{pubkey_b, pubkey_c}, new String[]{round1_msg1});
            String round2_msg1 = Musig2.getRound2Msg(round1_state1, sighash, private_c, new String[]{pubkey_b, pubkey_c}, new String[]{round1_msg0});
            String multi_signature = Musig2.getAggSignature(new String[]{round2_msg0, round2_msg1});
            // multi_signature = "2639d4d9882f6e7e42db38dbd2845c87b131737bf557643ef575c49f8fc6928869d9edf5fd61606fb07cced365fdc2c7b637e6ecc85b29906c16d314e7543e94";
            System.out.println("current multi_signature:" + multi_signature);
            String control_block = Mast.generateControlBlock(new String[]{pubkey_a, pubkey_b, pubkey_c}, (byte) 2,  pubkey_bc);
            System.out.println("current control_block:" + control_block);
            final_tx = Transaction.buildThresholdTx(base_tx, multi_signature, pubkey_bc, control_block, txids[i], input_indexs[i]);
            System.out.println("current threshold_tx:" + final_tx);
        }
        System.out.println("final threshold_tx:" + final_tx);

        // other tool func test
        String script_pubkey = Transaction.getScriptPubkey("tb1pn202yeugfa25nssxk2hv902kmxrnp7g9xt487u256n20jgahuwasdcjfdw");
        System.out.println("script_pubkey:" + script_pubkey);
        prev_txs = new String[]{"020000000001014be640313b023c3c731b7e89c3f97bebcebf9772ea2f7747e5604f4483a447b601000000000000000002a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bbc027090000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01404dc68b31efc1468f84db7e9716a84c19bbc53c2d252fd1d72fa6469e860a74486b0990332b69718dbcb5acad9d48634d23ee9c215ab15fb16f4732bed1770fdf00000000", "02000000000101aeee49e0bbf7a36f78ea4321b5c8bae0b8c72bdf2c024d2484b137fa7d0f8e1f01000000000000000003a0860100000000002251209a9ea267884f5549c206b2aec2bd56d98730f90532ea7f7154d4d4f923b7e3bb0000000000000000326a3035516a706f3772516e7751657479736167477a6334526a376f737758534c6d4d7141754332416255364c464646476a38801a060000000000225120c9929543dfa1e0bb84891acd47bfa6546b05e26b7a04af8eb6765fcc969d565f01409e325889515ed47099fdd7098e6fafdc880b21456d3f368457de923f4229286e34cef68816348a0581ae5885ede248a35ac4b09da61a7b9b90f34c200872d2e300000000"};
        input_indexs = new long[]{1, 0};
        String spend_outputs = Transaction.generateSpentOutputs(prev_txs, input_indexs);
        System.out.println("spend_outputs:" + spend_outputs);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}