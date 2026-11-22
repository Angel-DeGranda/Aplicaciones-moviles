package com.example.fakewhatsapp;

// Fase 12: los valores vienen de secrets.properties (ver app/build.gradle.kts),
// así no quedan escritos en el código ni se suben al repositorio.
public class SupabaseConfig {
    public static final String URL = BuildConfig.SUPABASE_URL;
    public static final String KEY = BuildConfig.SUPABASE_KEY;
}
