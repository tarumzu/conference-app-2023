package io.github.droidkaigi.confsched2023.data.sponsors

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.GET
import io.github.droidkaigi.confsched2023.data.NetworkService
import io.github.droidkaigi.confsched2023.data.sponsors.response.SponsorResponse
import io.github.droidkaigi.confsched2023.data.sponsors.response.SponsorsResponse
import io.github.droidkaigi.confsched2023.model.Plan
import io.github.droidkaigi.confsched2023.model.Sponsor
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal interface ContributorApi {
    @GET("/events/droidkaigi2023/sponsor")
    suspend fun getSponsors(): SponsorsResponse
}

public class DefaultSponsorsApiClient(
    val networkService: NetworkService,
    ktorfit: Ktorfit,
) : SponsorsApiClient {

    private val contributorApi = ktorfit.create<ContributorApi>()
    public override suspend fun sponsors(): PersistentList<Sponsor> {
        return networkService {
            contributorApi.getSponsors()
        }.toSponsorList()
    }
}

interface SponsorsApiClient {
    suspend fun sponsors(): PersistentList<Sponsor>
}

private fun SponsorsResponse.toSponsorList(): PersistentList<Sponsor> {
    return sponsor.map { it.toSponsor() }.toPersistentList()
}

private fun SponsorResponse.toSponsor(): Sponsor {
    return Sponsor(
        name = sponsorName,
        logo = sponsorLogo,
        plan = Plan.ofOrNull(plan) ?: Plan.SUPPORTER,
        link = link,
    )
}
