package mk.stole.restoran.data.to

import java.io.Serializable

data class Meni(
    var SlednoNivoTip: Int,
    var Tip: Int,
    var Sifra_ID: Int,
    var Naziv: String,
    var Cena: Double,
    var StandardnoMeni: Int,
    var Tatko_ID: Int,
    var TatkoNaziv: String
) : Serializable
