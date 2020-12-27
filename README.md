# Popup-Toast

[![API](https://img.shields.io/badge/API-19%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=19)
[![](https://jitpack.io/v/utkarsh13/PopupToast.svg)](https://jitpack.io/#utkarsh13/PopupToast)

Displays a toast similar to Reddit app

<img src="https://user-images.githubusercontent.com/8288422/103168782-2d5a0f00-485c-11eb-9210-13035dbc8822.gif" height = "460">


<img src="https://user-images.githubusercontent.com/8288422/103168977-01d82400-485e-11eb-9322-fffc23e35a0e.png" height = "100"> <img src="https://user-images.githubusercontent.com/8288422/103168980-0ef51300-485e-11eb-8853-01528bda4b17.png" height = "100">

<img src="https://user-images.githubusercontent.com/8288422/103169037-7e6b0280-485e-11eb-9e88-e3b91bab25e5.png" height = "100"> <img src="https://user-images.githubusercontent.com/8288422/103169039-8034c600-485e-11eb-99a2-7c989e1dd311.png" height = "100">


## Prerequisites

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```


## Dependency

Add this to your module's `build.gradle` file (make sure the version matches the JitPack badge above):

```gradle
dependencies {
	...
	implementation 'com.github.utkarsh13:PopupToast:1.0.0'
}
```

## Usage

All the PopupToasts require **activity context**

To display an Info Toast:

``` java
PopupToast(this)
    .makeText("This is an info message.")
    .setStyle(ToastStyle.INFO)
    .show()
```
To display an Success Toast:

``` java
PopupToast(this)
    .makeText("This is a success message.")
    .setStyle(ToastStyle.SUCCESS)
    .show()
```
To display an Warning Toast:

``` java
PopupToast(this)
    .makeText("This is a warning message.")
    .setStyle(ToastStyle.WARNING)
    .show()
```
To display an Error Toast:

``` java
PopupToast(this)
    .makeText("This is a error message.")
    .setStyle(ToastStyle.ERROR)
    .show()
```
To display an Custom style Toast:

``` java
PopupToast(this)
    .makeText("This is a custom style message in two lines", Color.GREEN)
    .setIcon(R.drawable.ic_success_white, Color.GREEN)
    .setDuration(5000)
    .setThemeColor(Color.RED)
    .setBgColor(Color.GRAY)
    .show()
```
To display an Custom view as a toast:

``` java
val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
val view = inflater.inflate(R.layout.view_custom, null)

PopupToast(this)
    .setView(view)
    .setDuration(6000)
    .show()
```

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/utkarsh13/PopupToast/pulls).

Any contributions, features, bug fixes are welcomed and appreciated but will be thoroughly reviewed.
