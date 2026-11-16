# Inventrar Design System

This design system provides a comprehensive set of custom composable wrappers that make it easy to create consistent, scalable, and easily modifiable UIs.

## Core Components

### CustomText
A flexible text component that wraps Material3's Text with additional customization options.

```kotlin
CustomText(
    text = "Hello World",
    size = 18.sp,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
)
```

### CustomButton
A customizable button component with various styling options.

```kotlin
CustomButton(
    onClick = { /* action */ },
    text = "Click Me",
    backgroundColor = PrimaryColor,
    contentColor = Color.White,
    enabled = true,
    shape = MaterialTheme.shapes.medium
)
```

### CustomCard
A flexible card component for containing content.

```kotlin
CustomCard(
    onClick = { /* optional click action */ },
    backgroundColor = MaterialTheme.colorScheme.surface,
    elevation = 4.dp,
    contentPadding = PaddingValues(16.dp)
) {
    // Your content here
}
```

### CustomTextField
A comprehensive text field component with all Material3 features.

```kotlin
CustomTextField(
    value = text,
    onValueChange = { text = it },
    label = { CustomText("Enter text") },
    placeholder = { CustomText("Placeholder") },
    isError = hasError,
    supportingText = { CustomText("Helper text") }
)
```

### CustomIcon & CustomIconButton
Icon components with consistent styling.

```kotlin
CustomIcon(
    imageVector = Icons.Default.Home,
    contentDescription = "Home",
    tint = PrimaryColor,
    size = 24.dp
)

CustomIconButton(
    onClick = { /* action */ }
) {
    CustomIcon(Icons.Default.Menu)
}
```

### CustomImage
Image component with resource support.

```kotlin
CustomImage(
    resource = Res.drawable.logo,
    contentDescription = "Logo",
    contentScale = ContentScale.Fit,
    colorFilter = ColorFilter.tint(PrimaryColor)
)
```

### CustomAlertDialog
Dialog component with customizable styling.

```kotlin
CustomAlertDialog(
    onDismissRequest = { /* dismiss */ },
    title = { HeadingText("Confirm Action") },
    text = { BodyText("Are you sure?") },
    confirmButton = {
        PrimaryButton(
            onClick = { /* confirm */ },
            text = "Confirm"
        )
    },
    dismissButton = {
        OutlinedPrimaryButton(
            onClick = { /* cancel */ },
            text = "Cancel"
        )
    }
)
```

### CustomProgressIndicator
Loading indicators with consistent styling.

```kotlin
CustomProgressIndicator(
    color = PrimaryColor,
    strokeWidth = 4.dp
)

CustomLoadingScreen(
    message = "Loading data...",
    color = PrimaryColor
)
```

## Design System Variants

### Button Variants
- `PrimaryButton` - Main action button with primary color
- `SecondaryButton` - Secondary action button
- `OutlinedPrimaryButton` - Outlined button with primary color

### Text Variants
- `HeadingText` - Large, bold text for headings
- `SubheadingText` - Medium, semi-bold text for subheadings
- `BodyText` - Regular body text
- `CaptionText` - Small text for captions and labels

## Color Palette

The design system uses a consistent color palette defined in `Colors.kt`:

- `PrimaryColor` - Main brand color (#2a73fd)
- `SecondaryColor` - Secondary brand color (#d4e4fe)
- `DarkBlue` - Dark blue accent (#0B405E)
- `DesertWhite` - Light background (#F7F7F7)
- `SandYellow` - Accent yellow (#FFBD64)
- `LightBlue` - Light blue accent (#9AD9FF)
- `CustomRed` - Error/warning color (#EF7370)
- `CustomGray` - Neutral gray (#F5F5F5)

## Usage Guidelines

### 1. Consistency
Always use the custom components instead of direct Material3 components to maintain consistency across the app.

### 2. Theming
The components automatically adapt to Material3's theme, but you can override colors when needed.

### 3. Accessibility
All components include proper content descriptions and accessibility features.

### 4. Scalability
To modify the design system:
1. Update the base custom components
2. Adjust color palette in `Colors.kt`
3. Modify design system variants
4. Changes will automatically apply across the entire app

## Migration Guide

### Replacing Text
```kotlin
// Before
Text("Hello", style = MaterialTheme.typography.headlineSmall)

// After
HeadingText("Hello")
// or
CustomText("Hello", fontWeight = FontWeight.Bold)
```

### Replacing Button
```kotlin
// Before
Button(onClick = {}) {
    Text("Click")
}

// After
PrimaryButton(onClick = {}, text = "Click")
// or
CustomButton(onClick = {}, text = "Click")
```

### Replacing Card
```kotlin
// Before
Card(modifier = Modifier.padding(16.dp)) {
    Text("Content")
}

// After
CustomCard(contentPadding = PaddingValues(16.dp)) {
    CustomText("Content")
}
```

## Benefits

1. **Consistency** - All UI elements follow the same design patterns
2. **Scalability** - Easy to modify the entire app's appearance by updating components
3. **Maintainability** - Centralized styling makes updates simple
4. **Performance** - Optimized components with proper state management
5. **Accessibility** - Built-in accessibility features
6. **Future-proof** - Easy to add new variants and themes

This design system provides the foundation for creating beautiful, consistent, and maintainable UIs that can evolve with your design requirements.





