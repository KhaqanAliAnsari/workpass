package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// ----------------------------------------------------
// MULTI-LANGUAGE TRANSLATION SYSTEM
// ----------------------------------------------------
var isHindi by mutableStateOf(false)

fun t(en: String, hi: String): String {
    return if (isHindi) hi else en
}

val skillTranslations = mapOf(
    "Electrician" to "बिजली मिस्त्री",
    "Plumber" to "नलसाज",
    "Painter" to "पेंटर",
    "Carpenter" to "बढ़ई",
    "Cleaner" to "सफाईकर्मी",
    "Mason" to "राजमिस्त्री"
)

fun translateSkill(skill: String): String {
    return if (isHindi) skillTranslations[skill] ?: skill else skill
}

val reviewTranslations = mapOf(
    "Great work, arrived on time" to "बहुत अच्छा काम, समय पर आए",
    "Fixed the issue quickly" to "समस्या को जल्दी ठीक कर दिया",
    "Good but a bit late" to "अच्छा था लेकिन थोड़ा देर से आए",
    "Very professional" to "बहुत ही पेशेवर"
)

fun translateReview(review: String): String {
    return if (isHindi) reviewTranslations[review] ?: review else review
}

val nameTranslations = mapOf(
    "Ramesh Kumar" to "रमेश कुमार",
    "Suresh Yadav" to "सुरेश यादव",
    "Anil Sharma" to "अनिल शर्मा",
    "Vijay Singh" to "विजय सिंह",
    "Deepak Verma" to "दीपक वर्मा",
    "Rohit Mishra" to "रोहित मिश्रा",
    "Amit Chauhan" to "अमित चौहान",
    "Sanjay Patil" to "संजय पाटिल"
)

fun translateName(name: String): String {
    return if (isHindi) nameTranslations[name] ?: name else name
}

@Composable
fun LanguageToggle(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFF0137CF).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .clickable { isHindi = !isHindi }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "EN",
            fontSize = 12.sp,
            fontWeight = if (!isHindi) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (!isHindi) Color(0xFF0137CF) else Color.Gray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "|",
            fontSize = 12.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "हिंदी",
            fontSize = 12.sp,
            fontWeight = if (isHindi) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isHindi) Color(0xFF0137CF) else Color.Gray
        )
    }
}

fun HomeTab.getTranslatedLabel(): String {
    return when (this) {
        HomeTab.PROFILE -> t("Profile", "प्रोफ़ाइल")
        HomeTab.MAP -> t("Map", "नक्शा")
        HomeTab.MATCH -> t("Hire", "किराए पर लें")
        HomeTab.CHAT -> t("Chat", "चैट")
    }
}

// ----------------------------------------------------
// WORKER MODEL & DATA STRUCTURES
// ----------------------------------------------------
data class Worker(
    val id: Int,
    val name: String,
    val skill: String,
    val rating: Float,
    val jobsDone: Int,
    val distanceKm: Float,
    val verified: Boolean
)

val skillTypes = listOf("Electrician", "Plumber", "Painter", "Carpenter", "Cleaner", "Mason")

val skillIcons = mapOf(
    "Electrician" to "⚡",
    "Plumber" to "🔧",
    "Painter" to "🎨",
    "Carpenter" to "🪚",
    "Cleaner" to "🧹",
    "Mason" to "🧱"
)

val fakeWorkers = listOf(
    Worker(1, "Ramesh Kumar", "Electrician", 4.8f, 132, 0.8f, true),
    Worker(2, "Suresh Yadav", "Plumber", 4.5f, 87, 1.2f, true),
    Worker(3, "Anil Sharma", "Painter", 4.2f, 54, 2.1f, false),
    Worker(4, "Vijay Singh", "Carpenter", 4.9f, 210, 1.6f, true),
    Worker(5, "Deepak Verma", "Cleaner", 4.3f, 41, 0.5f, true),
    Worker(6, "Rohit Mishra", "Mason", 4.6f, 98, 3.0f, false),
    Worker(7, "Amit Chauhan", "Electrician", 4.1f, 22, 2.7f, true),
    Worker(8, "Sanjay Patil", "Plumber", 4.7f, 156, 0.4f, true)
)

val fakeReviews = listOf(
    "Great work, arrived on time" to 5,
    "Fixed the issue quickly" to 5,
    "Good but a bit late" to 4,
    "Very professional" to 5
)

// ----------------------------------------------------
// ANIMATION UTILITIES
// ----------------------------------------------------
fun Modifier.bounceClick(): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "bounceClickScale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    when (event.type) {
                        PointerEventType.Press -> pressed = true
                        PointerEventType.Release, PointerEventType.Exit -> pressed = false
                        else -> {}
                    }
                }
            }
        }
}

@Composable
fun StaggeredListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val offsetPx = with(LocalDensity.current) { 20.dp.toPx() }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "staggerAlpha"
    )
    val translateY by animateFloatAsState(
        targetValue = if (visible) 0f else offsetPx,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
        label = "staggerTranslateY"
    )

    LaunchedEffect(Unit) {
        delay(index * 40L)
        visible = true
    }

    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = alpha
            translationY = translateY
        }
    ) {
        content()
    }
}

// ----------------------------------------------------
// MAIN ACTIVITY ENTRY POINT
// ----------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppRoot()
            }
        }
    }
}

// ----------------------------------------------------
// NAVIGATION CONFIGURATION
// ----------------------------------------------------
enum class Screen { SPLASH, LOGIN, SIGNUP, HOME }

enum class HomeTab(val label: String, val icon: ImageVector, val tag: String) {
    PROFILE("Profile", Icons.Filled.Person, "tab_profile"),
    MAP("Map", Icons.Filled.Place, "tab_map"),
    MATCH("Hire", Icons.Filled.Search, "tab_match"),
    CHAT("Chat", Icons.Filled.Email, "tab_chat")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    var screen by remember { mutableStateOf(Screen.SPLASH) }
    var tab by remember { mutableStateOf(HomeTab.PROFILE) }
    var selectedWorkerForChat by remember { mutableStateOf<Worker?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (screen) {
            Screen.SPLASH -> {
                SplashScreen(onDone = { screen = Screen.LOGIN })
            }

            Screen.LOGIN -> {
                LoginScreen(
                    onLogin = { screen = Screen.HOME },
                    onGoSignup = { screen = Screen.SIGNUP }
                )
            }

            Screen.SIGNUP -> {
                SignupScreen(
                    onDone = { screen = Screen.HOME },
                    onBack = { screen = Screen.LOGIN }
                )
            }

            Screen.HOME -> {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    modifier = Modifier.offset(x = (-16).dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_workpass_logo_blue_1784460377001),
                                        contentDescription = "WorkPass Logo",
                                        modifier = Modifier.size(84.dp).clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "WORKPASS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 24.sp,
                                        color = Color(0xFF0137CF),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.White,
                                titleContentColor = Color(0xFF0137CF)
                            ),
                            modifier = Modifier
                                .height(112.dp)
                                .shadow(8.dp)
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF0137CF), // Brand Blue
                            tonalElevation = 16.dp,
                            modifier = Modifier.height(92.dp)
                        ) {
                            HomeTab.values().forEach { t ->
                                val isSelected = tab == t
                                val tintColor = if (isSelected) Color.Black else Color.White
                                
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { tab = t },
                                    icon = {
                                        val iconScale by animateFloatAsState(
                                            targetValue = if (isSelected) 1.15f else 1f,
                                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                            label = "navIconScale"
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Icon(
                                            imageVector = t.icon,
                                            contentDescription = t.getTranslatedLabel(),
                                            tint = tintColor,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .graphicsLayer {
                                                    scaleX = iconScale
                                                    scaleY = iconScale
                                                }
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = t.getTranslatedLabel(),
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = tintColor
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        selectedTextColor = Color.Black,
                                        unselectedIconColor = Color.White,
                                        unselectedTextColor = Color.White,
                                        indicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .testTag(t.tag)
                                        .padding(top = 6.dp)
                                )
                            }
                        }
                    },
                    containerColor = Color.White
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color.White)
                    ) {
                        AnimatedContent(
                            targetState = tab,
                            transitionSpec = {
                                val offsetSpring = spring<IntOffset>(
                                    dampingRatio = 0.85f,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                                val alphaSpring = spring<Float>(
                                    dampingRatio = 0.85f,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                                if (targetState.ordinal > initialState.ordinal) {
                                    slideInHorizontally(animationSpec = offsetSpring) { width -> width } + fadeIn(animationSpec = alphaSpring) togetherWith
                                        slideOutHorizontally(animationSpec = offsetSpring) { width -> -width } + fadeOut(animationSpec = alphaSpring)
                                } else {
                                    slideInHorizontally(animationSpec = offsetSpring) { width -> -width } + fadeIn(animationSpec = alphaSpring) togetherWith
                                        slideOutHorizontally(animationSpec = offsetSpring) { width -> width } + fadeOut(animationSpec = alphaSpring)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) { targetTab ->
                            when (targetTab) {
                                HomeTab.PROFILE -> ProfileScreen()
                                HomeTab.MAP -> {
                                    MapScreen(
                                        onSelectWorkerForChat = { worker ->
                                            selectedWorkerForChat = worker
                                            tab = HomeTab.CHAT
                                        }
                                    )
                                }
                                HomeTab.MATCH -> {
                                    MatchScreen(
                                        onHireWorker = { worker ->
                                            selectedWorkerForChat = worker
                                            tab = HomeTab.CHAT
                                        }
                                    )
                                }
                                HomeTab.CHAT -> {
                                    ChatPaymentScreen(activeWorker = selectedWorkerForChat)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Language Toggle in top right
        if (screen != Screen.SPLASH) {
            LanguageToggle(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 16.dp)
                    .zIndex(100f)
            )
        }
    }
}

// ----------------------------------------------------
// SCREEN 1: SPLASH SCREEN (WITH SPRING SCALE)
// ----------------------------------------------------
@Composable
fun SplashScreen(onDone: () -> Unit) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        delay(1200)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.img_workpass_logo_blue_1784460377001),
                contentDescription = "WorkPass Logo",
                modifier = Modifier.size((96 * scale.value).dp).padding(bottom = 16.dp).clip(RoundedCornerShape(12.dp))
            )
            Text(
                text = "WorkPass",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0137CF), // Brand Blue Accent
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = t("Find work. Find workers.", "काम ढूंढें। कामगार ढूंढें।"),
                fontSize = 15.sp,
                color = Color(0xFF002280), // Dark blue depth
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ----------------------------------------------------
// SCREEN 2: LOGIN SCREEN
// ----------------------------------------------------
@Composable
fun LoginScreen(onLogin: () -> Unit, onGoSignup: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var aadhar by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically { it / 2 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_workpass_logo_blue_1784460377001),
                    contentDescription = "WorkPass Logo",
                    modifier = Modifier.size(96.dp).padding(bottom = 8.dp).clip(RoundedCornerShape(12.dp))
                )
                Text(
                    text = t("Welcome back", "स्वागत है"),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0137CF), // Brand Blue Accent
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = t("Log in to connect with trusted labor", "विश्वसनीय कामगारों से जुड़ने के लिए लॉग इन करें"),
                    fontSize = 14.sp,
                    color = Color(0xFF002280), // Dark blue depth
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Card(
                    modifier = Modifier
                        .bounceClick()
                        .fillMaxWidth()
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) phone = it },
                            label = { Text(t("Phone number", "फ़ोन नंबर"), color = Color(0xFF002280)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                cursorColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_phone_input")
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = aadhar,
                            onValueChange = { if (it.length <= 12 && it.all { char -> char.isDigit() }) aadhar = it },
                            label = { Text(t("Aadhar number", "आधार नंबर"), color = Color(0xFF002280)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                focusedLabelColor = Color.Black,
                                cursorColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_aadhar_input")
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Button(
                            onClick = {
                                val isValidPhone = phone.length == 10 && phone.firstOrNull() in listOf('6', '7', '8', '9')
                                val isValidAadhar = aadhar.length == 12 && aadhar.all { it.isDigit() }
                                if (isValidPhone && isValidAadhar) {
                                    onLogin()
                                } else {
                                    showError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0137CF),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .bounceClick()
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_submit_button")
                        ) {
                            Text(
                                text = t("Login", "लॉग इन करें"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                TextButton(
                    onClick = onGoSignup,
                    modifier = Modifier.testTag("signup_redirect_button")
                ) {
                    Text(
                        text = t("New here? Create an account", "नए हैं? खाता बनाएं"),
                        color = Color(0xFF002280),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (showError) {
            AlertDialog(
                onDismissRequest = {
                    showError = false
                    phone = ""
                    aadhar = ""
                },
                title = { Text(t("Invalid Credentials", "अमान्य क्रेडेंशियल")) },
                text = { Text(t("Please enter a valid 10-digit phone number (starting with 6-9) and a 12-digit Aadhar number.", "कृपया एक वैध 10-अंकीय फ़ोन नंबर (6-9 से शुरू होने वाला) और 12-अंकीय आधार नंबर दर्ज करें।")) },
                confirmButton = {
                    TextButton(onClick = {
                        showError = false
                        phone = ""
                        aadhar = ""
                    }) {
                        Text(t("OK", "ठीक है"))
                    }
                }
            )
        }
    }
}

// ----------------------------------------------------
// SCREEN 3: SIGNUP SCREEN (DIGILOCKER VERIFICATION)
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(onDone: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var skillExpanded by remember { mutableStateOf(false) }
    var skill by remember { mutableStateOf(skillTypes.first()) }
    var verifying by remember { mutableStateOf(false) }
    var verified by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("signup_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = t("Go Back", "वापस जाएं"),
                    tint = Color(0xFF0137CF)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = t("Create your worker profile", "अपना कामगार प्रोफ़ाइल बनाएं"),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0137CF)
        )
        Text(
            text = t("Verify identity with DigiLocker to start receiving high-paying jobs", "अच्छे वेतन वाले काम पाने के लिए डिजीलॉकर से पहचान सत्यापित करें"),
            fontSize = 13.sp,
            color = Color(0xFF002280),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier
                .bounceClick()
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(t("Full name", "पूरा नाम"), color = Color(0xFF002280)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_name_input")
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10) phone = it },
                    label = { Text(t("Phone number", "फ़ोन नंबर"), color = Color(0xFF002280)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_phone_input")
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = skillExpanded,
                    onExpandedChange = { skillExpanded = it }
                ) {
                    OutlinedTextField(
                        value = translateSkill(skill),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(t("Primary skill", "प्राथमिक कौशल"), color = Color(0xFF002280)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = skillExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = skillExpanded,
                        onDismissRequest = { skillExpanded = false }
                    ) {
                        skillTypes.forEach { skillItem ->
                            DropdownMenuItem(
                                text = { Text(translateSkill(skillItem)) },
                                onClick = {
                                    skill = skillItem
                                    skillExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (!verified) {
                    Button(
                        onClick = {
                            verifying = true
                            scope.launch {
                                delay(1500)
                                verifying = false
                                verified = true
                            }
                        },
                        enabled = !verifying && name.isNotBlank() && phone.length == 10,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF002280),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .bounceClick()
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("digilocker_verify_button")
                    ) {
                        if (verifying) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(t("Verifying with DigiLocker...", "डिजीलॉकर से सत्यापित किया जा रहा है..."), color = Color.White)
                        } else {
                            Text(t("Verify identity (DigiLocker)", "पहचान सत्यापित करें (डिजीलॉकर)"))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t("Verified via DigiLocker", "डिजीलॉकर के माध्यम से सत्यापित"), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            enabled = verified,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0137CF),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .bounceClick()
                .fillMaxWidth()
                .height(54.dp)
                .testTag("signup_finish_button")
        ) {
            Text(t("Finish & Continue", "समाप्त करें और जारी रखें"), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ----------------------------------------------------
// SCREEN 4: PROFILE SCREEN
// ----------------------------------------------------
@Composable
fun ProfileScreen() {
    val worker = fakeWorkers.first()
    var playingVoice by remember { mutableStateOf(false) }
    var descriptionText by remember { mutableStateOf("") }
    var uploadStatusMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                modifier = Modifier
                    .bounceClick()
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)), // Modern soft gray avatar background (Mockup 1)
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Avatar",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = translateName(worker.name),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "⚡ ${translateSkill(worker.skill)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0137CF) // Brand Blue
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (worker.verified) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified Badge",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = t("Govt ID Verified", "सरकारी आईडी सत्यापित"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = t("⭐ ${worker.rating}  •  ${worker.jobsDone} jobs completed", "⭐ ${worker.rating}  •  ${worker.jobsDone} काम पूरे किए गए"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
        
        item {
            Text(
                text = t("Recent Reviews", "हाल की समीक्षाएं"),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0137CF), // Brand Blue
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        itemsIndexed(fakeReviews) { index, (review, stars) ->
            StaggeredListItem(index = index) {
                Card(
                    modifier = Modifier
                        .bounceClick()
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.padding(bottom = 4.dp)) {
                            repeat(stars) {
                                Text("⭐", fontSize = 14.sp)
                            }
                        }
                        Text(
                            text = translateReview(review),
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = t("Previous Work", "पिछला काम"),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0137CF), // Brand Blue
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier
                    .bounceClick()
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Upload Area Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable {
                                // Simulate image picking
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Share, // acts as a representation of cloud/upload sharing
                                contentDescription = "Upload Icon",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = t("Select photo to upload", "अपलोड करने के लिए फ़ोटो चुनें"),
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description text input
                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text(t("Description", "विवरण"), color = Color(0xFF002280)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (descriptionText.isNotBlank()) {
                                uploadStatusMessage = t("Successfully uploaded previous work!", "पिछला काम सफलतापूर्वक अपलोड किया गया!")
                                descriptionText = ""
                            } else {
                                uploadStatusMessage = t("Please fill in description first", "कृपया पहले विवरण भरें")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0137CF),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .bounceClick()
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(t("Upload", "अपलोड करें"), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (uploadStatusMessage != null) {
        AlertDialog(
            onDismissRequest = { uploadStatusMessage = null },
            confirmButton = {
                TextButton(onClick = { uploadStatusMessage = null }) {
                    Text(t("OK", "ठीक है"), color = Color(0xFF0137CF))
                }
            },
            title = { Text(t("Upload Status", "अपलोड स्थिति")) },
            text = { Text(uploadStatusMessage ?: "") }
        )
    }
}

// ----------------------------------------------------
// SCREEN 5: MAP SCREEN (INTERACTIVE MULTI-GESTURE)
// ----------------------------------------------------
private data class LaborPin(val x: Float, val y: Float, val worker: Worker)

@Composable
fun MapScreen(onSelectWorkerForChat: (Worker) -> Unit) {
    var scale by remember { mutableStateOf(3.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var pins by remember { mutableStateOf(listOf<LaborPin>()) }
    var selectedPin by remember { mutableStateOf<LaborPin?>(null) }
    val zoomThreshold = 4.0f

    LaunchedEffect(scale >= zoomThreshold) {
        if (scale >= zoomThreshold) {
            pins = fakeWorkers.mapIndexed { idx, worker ->
                val angle = idx * (2 * Math.PI / fakeWorkers.size)
                val radius = 80f + idx * 25f
                LaborPin(
                    x = (150f + Math.cos(angle) * radius).toFloat(),
                    y = (250f + Math.sin(angle) * radius).toFloat(),
                    worker = worker
                )
            }
        } else {
            pins = emptyList()
            selectedPin = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1f, 6f)
                        // Clamp offset to prevent map from going too far off-screen
                        val maxOffset = (newScale - 1f) * 500f
                        val newOffsetX = (offset.x + pan.x).coerceIn(-maxOffset, maxOffset)
                        val newOffsetY = (offset.y + pan.y).coerceIn(-maxOffset, maxOffset)
                        scale = newScale
                        offset = Offset(newOffsetX, newOffsetY)
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .testTag("map_canvas")
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_city_map_1784407945221),
                contentDescription = "City Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            pins.forEach { pin ->
                val emoji = skillIcons[pin.worker.skill] ?: "🛠️"
                AnimatedVisibility(
                    visible = scale >= zoomThreshold,
                    enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                    modifier = Modifier.offset(x = pin.x.dp, y = pin.y.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = if (selectedPin == pin) Color(0xFF0137CF) else Color(0xFF002280),
                                shape = CircleShape
                            )
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable { selectedPin = pin }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = emoji,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Zoom + and - Controls Card (Overlay on the right side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp))
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = {
                    scale = (scale + 0.5f).coerceIn(1.0f, 6.0f)
                },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("zoom_in_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = t("Zoom In", "ज़ूम इन"),
                    tint = Color(0xFF0137CF)
                )
            }
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )
            IconButton(
                onClick = {
                    scale = (scale - 0.5f).coerceIn(1.0f, 6.0f)
                },
                modifier = Modifier
                    .size(40.dp)
                    .testTag("zoom_out_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = t("Zoom Out", "ज़ूम आउट"),
                    tint = Color(0xFF0137CF)
                )
            }
        }

        Card(
            modifier = Modifier
                .bounceClick()
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(0.9f)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.5.dp, Color(0xFF002280))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF0137CF),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (scale >= zoomThreshold) t("Workers found! Tap a pin to see details.", "कामगार मिल गए! विवरण देखने के लिए पिन पर टैप करें।") else t("Zoom in past 400% to discover local workers", "स्थानीय कामगारों को खोजने के लिए 400% से अधिक ज़ूम करें"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }

        AnimatedVisibility(
            visible = selectedPin != null,
            enter = slideInVertically { it } + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            selectedPin?.let { pin ->
                val worker = pin.worker
                Card(
                    modifier = Modifier
                        .bounceClick()
                        .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = translateName(worker.name),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = t("⚡ ${translateSkill(worker.skill)} • ${worker.distanceKm} km away", "⚡ ${translateSkill(worker.skill)} • ${worker.distanceKm} किमी दूर"),
                                    fontSize = 13.sp,
                                    color = Color(0xFF0137CF),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = t("⭐ ${worker.rating} (${worker.jobsDone} Jobs Done)", "⭐ ${worker.rating} (${worker.jobsDone} काम पूरे किए)"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            if (worker.verified) {
                                Text(
                                    text = t("✅ Govt Verified", "✅ सरकारी सत्यापित"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row {
                            Button(
                                onClick = { selectedPin = null },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEEEEEE),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .bounceClick()
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(t("Close", "बंद करें"), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    onSelectWorkerForChat(worker)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0137CF),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .bounceClick()
                                    .weight(1.5f)
                                    .height(44.dp)
                                    .testTag("chat_pin_button")
                            ) {
                                Text(t("Message & Pay", "संदेश और भुगतान"), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 6: MATCH SCREEN
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(onHireWorker: (Worker) -> Unit) {
    var skillExpanded by remember { mutableStateOf(false) }
    var selectedSkill by remember { mutableStateOf("") }
    var searching by remember { mutableStateOf(false) }
    var matched by remember { mutableStateOf<Worker?>(null) }
    val scope = rememberCoroutineScope()

    val filtered = if (selectedSkill.isEmpty()) emptyList() else fakeWorkers.filter { it.skill == selectedSkill }.sortedBy { it.distanceKm }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = t("Book a worker", "कामगार बुक करें"),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0137CF)
        )
        Text(
            text = t("Select work type and request verified local labor immediately.", "कार्य प्रकार चुनें और तुरंत सत्यापित स्थानीय कामगार का अनुरोध करें।"),
            fontSize = 13.sp,
            color = Color(0xFF002280),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ExposedDropdownMenuBox(
            expanded = skillExpanded,
            onExpandedChange = { skillExpanded = it }
        ) {
            OutlinedTextField(
                value = if (selectedSkill.isEmpty()) t("Select", "चुनें") else translateSkill(selectedSkill),
                onValueChange = {},
                readOnly = true,
                label = { Text(t("Type of work", "कार्य का प्रकार"), color = Color(0xFF002280)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = skillExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    cursorColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = skillExpanded,
                onDismissRequest = { skillExpanded = false }
            ) {
                skillTypes.forEach { skill ->
                    DropdownMenuItem(
                        text = { Text(translateSkill(skill)) },
                        onClick = {
                            selectedSkill = skill
                            skillExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (selectedSkill.isEmpty()) {
                t("Nearby workers (no work selected)", "आस-पास के कामगार (कोई काम नहीं चुना गया)")
            } else {
                t("Nearby ${translateSkill(selectedSkill)}s (${filtered.size} active)", "आस-पास के ${translateSkill(selectedSkill)} (${filtered.size} सक्रिय)")
            },
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF002280),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(filtered) { index, worker ->
                StaggeredListItem(index = index) {
                    Card(
                        modifier = Modifier
                            .bounceClick()
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = translateName(worker.name),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    if (worker.verified) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "✔",
                                            color = Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = t("⭐ ${worker.rating}  •  ${worker.distanceKm} km away  •  ${worker.jobsDone} jobs", "⭐ ${worker.rating}  •  ${worker.distanceKm} किमी दूर  •  ${worker.jobsDone} काम"),
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                            }
                            Button(
                                onClick = {
                                    searching = true
                                    scope.launch {
                                        delay(2000)
                                        searching = false
                                        matched = worker
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0137CF),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .bounceClick()
                                    .testTag("request_worker_${worker.id}")
                            ) {
                                Text(t("Request", "अनुरोध"))
                            }
                        }
                    }
                }
            }
        }
    }

    if (searching) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .bounceClick()
                    .shadow(16.dp, shape = RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFF0137CF))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF0137CF),
                        strokeWidth = 4.dp
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = t("Finding the nearest worker...", "निकटतम कामगार खोज रहे हैं..."),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = t("Matching you with highest rating ${selectedSkill.lowercase()}", "आपको उच्चतम रेटिंग वाले ${translateSkill(selectedSkill)} के साथ मिलाया जा रहा है"),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    matched?.let { worker ->
        Dialog(onDismissRequest = { matched = null }) {
            Card(
                modifier = Modifier
                    .bounceClick()
                    .shadow(16.dp, shape = RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFF2E7D32))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = t("Worker found! ✅", "कामगार मिल गया! ✅"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = translateName(worker.name),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Text(
                        text = t("${skillIcons[worker.skill]} ${translateSkill(worker.skill)} • Arriving in ~${(worker.distanceKm * 6).toInt()} min", "${skillIcons[worker.skill]} ${translateSkill(worker.skill)} • ~${(worker.distanceKm * 6).toInt()} मिनट में पहुंच रहे हैं"),
                        fontSize = 14.sp,
                        color = Color(0xFF002280),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val activeWorker = matched
                            matched = null
                            if (activeWorker != null) {
                                onHireWorker(activeWorker)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0137CF)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .bounceClick()
                            .fillMaxWidth()
                    ) {
                        Text(t("Open Chat & Escrow", "चैट और एस्क्रो खोलें"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 7: CHAT & ESCROW PAYMENT SCREEN
// ----------------------------------------------------
private data class ChatMessage(val fromMe: Boolean, val text: String)

@Composable
fun ChatPaymentScreen(activeWorker: Worker?) {
    val worker = activeWorker ?: fakeWorkers.first()
    
    val messages = remember {
        listOf(
            ChatMessage(false, "I'm 10 min away"),
            ChatMessage(true, "Sure, waiting at the gate"),
            ChatMessage(false, "Reached, starting the work")
        )
    }
    
    val escrowAmount = remember(worker.id) { (800..1200).random() }
    var otp by remember { mutableStateOf("") }
    var released by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .bounceClick()
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = translateName(worker.name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Text(
                        text = t("+91 8XX-XXX-4521 (masked)", "+91 8XX-XXX-4521 (छिपा हुआ)"),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF002280)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.bounceClick()
                ) {
                    Text(t("Call", "कॉल"), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages) { msg ->
                val translatedText = when (msg.text) {
                    "I'm 10 min away" -> t("I'm 10 min away", "मैं 10 मिनट में आ रहा हूँ")
                    "Sure, waiting at the gate" -> t("Sure, waiting at the gate", "ज़रूर, गेट पर प्रतीक्षा कर रहा हूँ")
                    "Reached, starting the work" -> t("Reached, starting the work", "पहुंच गया, काम शुरू कर रहा हूँ")
                    else -> msg.text
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = if (msg.fromMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.fromMe) Color(0xFF0137CF) else Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (msg.fromMe) 12.dp else 0.dp,
                            bottomEnd = if (msg.fromMe) 0.dp else 12.dp
                        ),
                        modifier = Modifier
                            .bounceClick()
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = translatedText,
                            color = if (msg.fromMe) Color.White else Color.Black,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .bounceClick()
                .fillMaxWidth()
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = t("Job amount held in escrow", "एस्क्रो में रखी गई काम की राशि"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF002280)
                )
                Text(
                    text = "₹$escrowAmount",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                if (!released) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 6) otp = it },
                        label = { Text(t("Enter completion OTP", "काम पूरा होने का ओटीपी दर्ज करें")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("escrow_otp_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { released = true },
                        enabled = otp.length == 6,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0137CF),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .bounceClick()
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("escrow_release_button")
                    ) {
                        Text(t("Confirm & Release Payment", "पुष्टि करें और भुगतान जारी करें"), fontWeight = FontWeight.Bold)
                    }
                } else {
                    val commission = (escrowAmount * 0.15).toInt()
                    val payout = escrowAmount - commission
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t("Payment released successfully", "भुगतान सफलतापूर्वक जारी किया गया"), color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = t("Worker payout: ₹$payout", "कामगार का भुगतान: ₹$payout"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = t("Platform fee (15%): ₹$commission", "प्लेटफ़ॉर्म शुल्क (15%): ₹$commission"),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
