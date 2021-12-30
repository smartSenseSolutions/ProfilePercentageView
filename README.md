# ProfilePercentageView


It is Profile Percentage View developed in Kotlin. It Highly customized view that offers multiple attributes to create a Percentage View based on your requirements.

## Demo


## Setup

Add the Maven Central repository to your project's root ```build.gradle``` file
``` gradle
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependency in your app ```build.gradle``` file:
``` gradle
implementation 'io.github.smartsensesolutions:ProfilePercentageView:1.0.0'

```
## Usage

Sample implementation [here](https://github.com/smartSenseSolutions/ProfilePercentageView/tree/main/app)

Use the view in your layout.xml by declaring:

``` xml
	<com.ss.profilepercentageview.ProfilePercentageView
                android:id="@+id/ppvProfile"
                android:layout_width="@dimen/_150sdp"
                android:layout_height="@dimen/_150sdp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                app:ppv_arcBackgroundColor="@color/arcBackgroundColor"
                app:ppv_arcProgressEndColor="@color/progressColor"
                app:ppv_arcProgressStartColor="@color/progressColor"
                app:ppv_arcWidth="@dimen/_6sdp"
                app:ppv_currentValue="72"
                app:ppv_dashWidth="@dimen/_2sdp"
                app:ppv_imgBorderColor="@color/borderColor"
                app:ppv_imgBorderWidth="@dimen/_3sdp"
                app:ppv_imgPadding="@dimen/_40sdp"
                app:ppv_isDashEnable="true"
                app:ppv_isRounded="false"
                app:ppv_max="100"
                app:srcCompat="@drawable/pic" />
```



## Attributes

Following are the various attributes available to customize the look and feel of the Seekbar.

**ppv_arcBackgroundColor:** Background Color of progress/arc base view.

**ppv_currentValue:** Current value of the Progress in progress/arc view.

**ppv_arcWidth:** Width of progress/arc view

**ppv_isDashEnable:** Dash progress/arc view enable or not.

**ppv_dashWidth:** Dash width of progress/arc view when dash progress enable.

**ppv_isRounded:** Shows rounded corner when dash is not enable.

**ppv_arcProgressStartColor:** Color of the progress in progress/arc view. It used of gradient effect.

**ppv_arcProgressEndColor:** Color of the progress in progress/arc view. It used of gradient effect.

**ppv_max:** Maximum value of progress/arc supported in view.

**srcCompat:** Image which should be display in centre of the progress/arc view.

**ppv_imgPadding:** Padding between progress view and image view. It will vary based on progress/arc width.

**ppv_imgBorderWidth:** Width of border/stroke around image.

**ppv_imgBorderColor:** Color of the border/stroke around image.
 

 
## License
```
Copyright 2021 SmartSense Solutions
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
```
