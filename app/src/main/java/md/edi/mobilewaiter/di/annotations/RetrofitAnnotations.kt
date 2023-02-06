package md.edi.mobilewaiter.di.annotations

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitAuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitNoAuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitPerformant