package com.example.decryptapp

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase

class CaesarCypherTest : TestCase() {

    private val tag = "CaesarCypherTest"

    fun testEncrypt() {
        /**
         * Paste the message to test here
         */
        val message = "hello world"

        /**
         * Place the key (shift value) to use here
         */
        val key = 0

        val result = CaesarCypher().encrypt(message, key)

        Log.d(tag, "testEncrypt() result: $result")
    }

    fun testDecrypt() {}

    fun testBruteForce() {
        /**
         * Paste the message to test here
         */
        val cyphertext =
            "P ohcl, tfzlsm, mbss jvumpklujl aoha pm hss kv aolpy kbaf, pm uvaopun pz ulnsljalk, huk pm aol ilza hyyhunltluaz hyl thkl, hz aolf hyl ilpun thkl, dl zohss wyvcl vbyzlsclz vujl hnhpu hisl av klmluk vby Pzshuk ovtl, av ypkl vba aol zavyt vm dhy, huk av vbaspcl aol tluhjl vm afyhuuf, pm uljlzzhyf mvy flhyz, pm uljlzzhyf hsvul. Ha huf yhal, aoha pz doha dl hyl nvpun av ayf av kv. Aoha pz aol ylzvscl vm Opz Thqlzaf'z Nvclyutlua, lclyf thu vm aolt. Aoha pz aol dpss vm Whysphtlua huk aol uhapvu. Aol Iypapzo Ltwpyl huk aol Mylujo Ylwbispj, spurlk avnlaoly pu aolpy jhbzl huk pu aolpy ullk, dpss klmluk av aol klhao aolpy uhapcl zvps, hpkpun lhjo vaoly sprl nvvk jvtyhklz av aol batvza vm aolpy zaylunao. Lclu aovbno shynl ayhjaz vm Lbyvwl huk thuf vsk huk mhtvbz Zahalz ohcl mhsslu vy thf mhss puav aol nypw vm aol Nlzahwv huk hss aol vkpvbz hwwhyhabz vm Uhgp ybsl, dl zohss uva mshn vy mhps. Dl zohss nv vu av aol luk, dl zohss mpnoa pu Myhujl, dl zohss mpnoa vu aol zlhz huk vjlhuz, dl zohss mpnoa dpao nyvdpun jvumpklujl huk nyvdpun zaylunao pu aol hpy, dl zohss klmluk vby Pzshuk, dohalcly aol jvza thf il, dl zohss mpnoa vu aol ilhjolz, dl zohss mpnoa vu aol shukpun nyvbukz, dl zohss mpnoa pu aol mplskz huk pu aol zayllaz, dl zohss mpnoa pu aol opssz; dl zohss ulcly zbyylukly, huk lclu pm, dopjo P kv uva mvy h tvtlua ilsplcl, aopz Pzshuk vy h shynl whya vm pa dlyl zbiqbnhalk huk zahycpun, aolu vby Ltwpyl ilfvuk aol zlhz, hytlk huk nbhyklk if aol Iypapzo Mslla, dvbsk jhyyf vu aol zaybnnsl, buaps, pu Nvk'z nvvk aptl, aol Uld Dvysk, dpao hss paz wvdly huk tpnoa, zalwz mvyao av aol ylzjbl huk aol spilyhapvu vm aol vsk."

        val result = CaesarCypher().bruteForce(
            cyphertext,
            true,
            InstrumentationRegistry.getInstrumentation().targetContext
        )

        Log.d(tag, "testBruteForce(): most likely candidate plaintext: ${result[0].str}")
    }
}