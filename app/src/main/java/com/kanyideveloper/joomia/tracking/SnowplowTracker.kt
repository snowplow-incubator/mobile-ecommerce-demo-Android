package com.kanyideveloper.joomia.tracking

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.kanyideveloper.joomia.feature_products.domain.model.Product
import com.snowplowanalytics.snowplow.Snowplow
import com.snowplowanalytics.snowplow.configuration.NetworkConfiguration
import com.snowplowanalytics.snowplow.configuration.SubjectConfiguration
import com.snowplowanalytics.snowplow.configuration.TrackerConfiguration
import com.snowplowanalytics.snowplow.controller.TrackerController
import com.snowplowanalytics.snowplow.ecommerce.entities.ProductEntity
import com.snowplowanalytics.snowplow.ecommerce.events.AddToCartEvent
import com.snowplowanalytics.snowplow.ecommerce.events.ProductViewEvent
import com.snowplowanalytics.snowplow.event.ScreenView
import com.snowplowanalytics.snowplow.event.SelfDescribing
import com.snowplowanalytics.snowplow.network.HttpMethod
import com.snowplowanalytics.snowplow.tracker.DevicePlatform
import com.snowplowanalytics.snowplow.tracker.LogLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SnowplowTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var trackerExists = false
    private val ipAddress = "192.168.0.25"
    
    private val subjectConfig = SubjectConfiguration()
    private val trackerConfig = TrackerConfiguration(context.packageName)
        .logLevel(LogLevel.VERBOSE)
    private val tracker = if (trackerExists) {
        Snowplow.defaultTracker
    } else {
        trackerExists = true
        Snowplow.createTracker(
            context,
            "ecommerce-demo",
            NetworkConfiguration("http://$ipAddress:9090"),
            subjectConfig,
            trackerConfig)
    }
    
    @Composable
    fun Track() {
        LaunchedEffect(Unit, block = {
            tracker?.track(
                SelfDescribing(
                    "iglu:com.snowplowanalytics.iglu/anything-a/jsonschema/1-0-0",
                    mapOf("example_key" to "example_value")
                )
            )
        })
    }

    @Composable
    fun TrackScreenView(name: String) {
        tracker?.track(ScreenView(name = name))
    }

    @Composable
    fun AutoTrackScreenView(navController: NavController) {
        LaunchedEffect(Unit, block = {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                tracker?.track(ScreenView(destination.route ?: "null"))
            }
        })
    }

    @Composable
    fun TrackProductView(product: Product) {
        LaunchedEffect(Unit, block = {
            tracker?.track(
                ProductViewEvent(ProductEntity(
                    id = product.id.toString(), 
                    category = product.category,
                    currency = "USD",
                    price = product.price,
                    name = product.title
                )
            ))
        })
    }

//    @Composable
//    fun TrackAddtoCart(product: Product) {
//        LaunchedEffect(Unit, block = {
//            tracker?.track(
//                AddToCartEvent
//                )
//        })
//    }
}
