package com.mobile.app_iara.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://ctrbebczhfwexyycuxko.supabase.co",
        supabaseKey = "sb_secret_poIduWoDXV3Dv7c6PxVzPQ_hyoBAHoG"
    ) {
        install(Postgrest)
        install(Storage)
    }
}