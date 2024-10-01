// noinspection JSUnusedGlobalSymbols,NpmUsedModulesInstalled,JSUnusedLocalSymbols

// @Caution by SuperMonster003 on Apr 19, 2022.
//  ! Do not declare with const because variable has already
//  ! declared globally in RhinoJavaScriptEngine.kt.
let global = this;

/* Global View classes. */

let JsAppBarLayout = org.ys.game.core.ui.widget.JsAppBarLayout;
let JsButton = org.ys.game.core.ui.widget.JsButton;
let JsCanvasView = org.ys.game.core.ui.widget.JsCanvasView;
let JsCardView = org.ys.game.core.ui.widget.JsCardView;
let JsCheckBox = org.ys.game.core.ui.widget.JsCheckBox;
let JsConsoleView = org.ys.game.core.ui.widget.JsConsoleView;
let JsDatePicker = org.ys.game.core.ui.widget.JsDatePicker;
let JsDrawerLayout = org.ys.game.core.ui.widget.JsDrawerLayout;
let JsEditText = org.ys.game.core.ui.widget.JsEditText;
let JsFloatingActionButton = org.ys.game.core.ui.widget.JsFloatingActionButton;
let JsFrameLayout = org.ys.game.core.ui.widget.JsFrameLayout;
let JsGridView = org.ys.game.core.ui.widget.JsGridView;
let JsImageButton = org.ys.game.core.ui.widget.JsImageButton;
let JsImageView = org.ys.game.core.ui.widget.JsImageView;
let JsLinearLayout = org.ys.game.core.ui.widget.JsLinearLayout;
let JsListView = org.ys.game.core.ui.widget.JsListView;
let JsProgressBar = org.ys.game.core.ui.widget.JsProgressBar;
let JsRadioButton = org.ys.game.core.ui.widget.JsRadioButton;
let JsRadioGroup = org.ys.game.core.ui.widget.JsRadioGroup;
let JsRatingBar = org.ys.game.core.ui.widget.JsRatingBar;
let JsRelativeLayout = org.ys.game.core.ui.widget.JsRelativeLayout;
let JsScrollView = org.ys.game.core.ui.widget.JsScrollView;
let JsSeekBar = org.ys.game.core.ui.widget.JsSeekBar;
let JsSpinner = org.ys.game.core.ui.widget.JsSpinner;
let JsSwitch = org.ys.game.core.ui.widget.JsSwitch;
let JsTabLayout = org.ys.game.core.ui.widget.JsTabLayout;
let JsTextClock = org.ys.game.core.ui.widget.JsTextClock;
let JsTextView = android.os.Build.VERSION.SDK_INT < 26 /* Android API 26 (8.0) [O] */
    ? org.ys.game.core.ui.widget.JsTextViewLegacy
    : org.ys.game.core.ui.widget.JsTextView;
let JsTimePicker = org.ys.game.core.ui.widget.JsTimePicker;
let JsToggleButton = org.ys.game.core.ui.widget.JsToggleButton;
let JsToolbar = org.ys.game.core.ui.widget.JsToolbar;
let JsViewPager = org.ys.game.core.ui.widget.JsViewPager;
let JsWebView = org.ys.game.core.ui.widget.JsWebView;

/* Global assignment. */

Object.assign(this, {
    isNullish(o) {
        // nullish coalescing operator: ??
        return o === null || o === undefined;
    },
    isObject(o) {
        return species(o) === 'Object';
    },
    isJavaClass(o) {
        return species(o) === 'JavaClass';
    },
    isJavaPackage(o) {
        return species(o) === 'JavaPackage';
    },
    isJavaObject(o) {
        if (o !== null && typeof o === 'object') {
            if (typeof o.getClass === 'function') {
                try {
                    return o.getClass() instanceof java.lang.Class;
                } catch (_) {
                    // Ignored.
                }
            }
        }
        return false;
    },
    isInteger(o) {
        return Number.isInteger(o);
    },
    isPrimitive(o) {
        // @Comment by SuperMonster003 on Apr 21, 2022.

        // return this.isNull(arg)
        //     || this.isBoolean(arg)
        //     || this.isNumber(arg)
        //     || this.isString(arg)
        //     || this.isSymbol(arg)
        //     || this.isUndefined(arg)
        //     || this.isBigInt(arg);

        return o !== Object(o);
    },
    isReference(o) {
        return o === Object(o);
    },
    isEmptyObject(obj) {
        // noinspection LoopStatementThatDoesntLoopJS
        for (let name in obj) {
            return false;
        }
        return true;
    },
    species: (/* @IIFE */ () => {
        const getSpecies = (o) => Object.prototype.toString.call(o)
            .slice('[Object\x20'.length, ']'.length * -1);

        function Species() {
            return Object.assign(o => getSpecies(o), Species.prototype);
        }

        Species.prototype.isArray = o => getSpecies(o) === 'Array';
        Species.prototype.isArrayBuffer = o => getSpecies(o) === 'ArrayBuffer';
        Species.prototype.isBigInt = o => getSpecies(o) === 'BigInt';
        Species.prototype.isBoolean = o => getSpecies(o) === 'Boolean';
        Species.prototype.isContinuation = o => getSpecies(o) === 'Continuation';
        Species.prototype.isDataView = o => getSpecies(o) === 'DataView';
        Species.prototype.isDate = o => getSpecies(o) === 'Date';
        Species.prototype.isError = o => getSpecies(o) === 'Error';
        Species.prototype.isFloat32Array = o => getSpecies(o) === 'Float32Array';
        Species.prototype.isFloat64Array = o => getSpecies(o) === 'Float64Array';
        Species.prototype.isFunction = o => getSpecies(o) === 'Function';
        Species.prototype.isHTMLDocument = o => getSpecies(o) === 'HTMLDocument';
        Species.prototype.isInt16Array = o => getSpecies(o) === 'Int16Array';
        Species.prototype.isInt32Array = o => getSpecies(o) === 'Int32Array';
        Species.prototype.isInt8Array = o => getSpecies(o) === 'Int8Array';
        Species.prototype.isJavaObject = o => getSpecies(o) === 'JavaObject';
        Species.prototype.isJavaPackage = o => getSpecies(o) === 'JavaPackage';
        Species.prototype.isMap = o => getSpecies(o) === 'Map';
        Species.prototype.isNamespace = o => getSpecies(o) === 'Namespace';
        Species.prototype.isNull = o => getSpecies(o) === 'Null';
        Species.prototype.isNumber = o => getSpecies(o) === 'Number';
        Species.prototype.isObject = o => getSpecies(o) === 'Object';
        Species.prototype.isQName = o => getSpecies(o) === 'QName';
        Species.prototype.isRegExp = o => getSpecies(o) === 'RegExp';
        Species.prototype.isSet = o => getSpecies(o) === 'Set';
        Species.prototype.isString = o => getSpecies(o) === 'String';
        Species.prototype.isUint16Array = o => getSpecies(o) === 'Uint16Array';
        Species.prototype.isUint32Array = o => getSpecies(o) === 'Uint32Array';
        Species.prototype.isUint8Array = o => getSpecies(o) === 'Uint8Array';
        Species.prototype.isUint8ClampedArray = o => getSpecies(o) === 'Uint8ClampedArray';
        Species.prototype.isUndefined = o => getSpecies(o) === 'Undefined';
        Species.prototype.isWeakMap = o => getSpecies(o) === 'WeakMap';
        Species.prototype.isWeakSet = o => getSpecies(o) === 'WeakSet';
        Species.prototype.isWindow = o => getSpecies(o) === 'Window';
        Species.prototype.isXML = o => getSpecies(o) === 'XML';
        Species.prototype.isXMLList = o => getSpecies(o) === 'XMLList';

        return new Species();
    })(),
    /**
     * @param {{getClass(): java.lang.Class<?>}} o
     * @return {*}
     */
    unwrapJavaObject(o) {

        /*
           @Hint by SuperMonster003 on Apr 21, 2022.
            ! Code below also works.
            ! However, IDE like WebStorm may not show the syntax highlighting
            ! for methods ("TypeScript Declarations" is needed)
        */
        // switch (o.getClass()) {
        //     case java.lang.Boolean:
        //         return o.booleanValue();
        //     ...
        // }

        if (o instanceof java.lang.Boolean) {
            return o.booleanValue();
        }

        if (o instanceof java.lang.Double) {
            return o.doubleValue();
        }

        if (o instanceof java.lang.Float) {
            return o.floatValue();
        }

        if (o instanceof java.lang.Integer) {
            return o.intValue();
        }

        if (o instanceof java.lang.Long) {
            return o.longValue();
        }

        if (o instanceof java.lang.Short) {
            return o.shortValue();
        }

        if (o instanceof java.lang.Character) {
            return o.charValue();
        }

        if (o instanceof java.lang.String) {
            return String(o);
        }

        return o;
    },
});

( /* @ModuleIIFE */ () => {

    const ProxyObject = org.ys.game.rhino.ProxyObject;
    const Pref = org.ys.game.pref.Pref;

    let _ = {
        Throwable: java.lang.Throwable,
        EvaluatorException: org.mozilla.javascript.EvaluatorException,
        moduleSpecials: {
            polyfill() {
                require('polyfill').fill();
            },
            json() {
                // @Commented by SuperMonster003 on May 24, 2022.
                //  ! Use internal Rhino JSON for better performance and compatibility.
                // _.define('JSON', require('json2'));
            },
            promise() {
                /**
                 * @Hint by SuperMonster003 on Apr 17, 2022.
                 * Try using internal Rhino Promise instead.
                 * Legacy Promise may be ignored by AutoJs6 engine when Promise was placed at the end.
                 *
                 * @example Code for reappearance
                 * let test = () => 'hello';
                 * Promise.resolve().then(test).then(res => log(res));
                 */
                /**
                 * @Hint by SuperMonster003 on May 24, 2022.
                 * Use updated external Promise from Rhino instead of internal Rhino Promise.
                 * Rhino Promise is not compatible with AutoJs6 continuation and causes a suspension.
                 * Also, updated Promise solved the problem mentioned on Apr 17, 2022.
                 *
                 * @example Code for reappearance
                 * AutoJs6/示例代码/协程/协程HelloWorld.js
                 */
                /**
                 * Substitution of Promise.
                 */
                _.define('Promise', require('promise'));
            },
        },
        cacheR: {},
        define(property, descriptorValue) {
            Object.defineProperty(global, property, { value: descriptorValue, enumerable: true });
        },
        bind(modules) {
            modules.forEach((module) => {
                if (Array.isArray(module)) {
                    this.bind(module);
                } else if (module in this.moduleSpecials) {
                    this.moduleSpecials[module].call();
                } else {
                    // Object.defineProperty(global, module, { value: require(`__${module}__`)(runtime, global), enumerable: true });
                    global[`$${module}`] = global[module] = require(`__${module}__`)(runtime, global);
                    try {
                        if (typeof global[module] === 'object') {
                            if (!(Object.hasOwn(global[module], 'toString'))) {
                                // noinspection JSPotentiallyInvalidConstructorUsage
                                let prototype = global[module].constructor.prototype;
                                if (prototype !== undefined) /* For AutoJs6 debugger. */ {
                                    if (typeof prototype.toString !== 'function') {
                                        global[`$${module}`].toString = global[module].toString = () => module;
                                    }
                                }
                            }
                        }
                    } catch (e) {
                        // Ignored.
                    }
                }
            });
        },
        getProxyObjectInstance(getter, setter) {
            return new ProxyObject(global, Object.assign(
                getter ? { get: getter } : {},
                setter ? { set: setter } : {},
            ));
        },
    };

    let $ = {
        init() {
            runtime.init();
            return this;
        },
        bind() {
            this.bindEpilogue();
            this.bindClasses();
            this.bindModules();
            this.bindPrologue();
        },
        bindEpilogue() {
            const __importClass__ = importClass;
            const __importPackage__ = importPackage;

            Object.assign(global, {
                // 重定向 importClass 使其支持字符串参数.
                /**
                 * @global
                 */
                importClass() {
                    Array.from(arguments).forEach(clazz => {
                        __importClass__(typeof clazz === 'string' ? Packages[clazz] : clazz);
                    });
                },
                // FIXME by SuperMonster003 on Jul 27, 2023.
                //  ! This makes importPackage() behave abnormally
                //  ! when calling it in a JavaScript module,
                //  ! even with "with" scope binding expression.
                //  !
                // // 重定向 importPackage 使其支持字符串参数.
                // /**
                //  * @global
                //  */
                // importPackage() {
                //     let args = Array.from(arguments);
                //     let expression =
                //         'args.forEach((pkg) => {' + '\n' +
                //         '    __importPackage__(typeof pkg === \'string\' ? Packages[pkg] : pkg);' + '\n' +
                //         '});';
                //     // @ScopeBinding
                //     // noinspection WithStatementJS
                //     with (context) {
                //         return (/* @IIFE */ function () {
                //             return eval(expression);
                //         })();
                //     }
                // },
                /**
                 * @global
                 */
                __asGlobal__(obj, functions, scope) {
                    if (typeof scope !== 'object') {
                        // @Overload __asGlobal__(obj: object, functions: (string | { [prop: string]: string })[]): void
                        return this.__asGlobal__(obj, functions, global);
                    }
                    functions.forEach((name) => {
                        let { objKey, scopeKey } = (/* @IIFE */ () => {
                            if (typeof name === 'string') {
                                let objKey = scopeKey = name;
                                return { objKey, scopeKey };
                            }
                            if (typeof name === 'object') {
                                let [ objKey ] = Object.values(name);
                                let [ scopeKey ] = Object.keys(name);
                                return { objKey, scopeKey };
                            }
                            throw TypeError(`Unknown type of name (${name}) in functions for __asGlobal__`);
                        })();
                        let f = obj[objKey];
                        if (typeof f !== 'function') {
                            throw ReferenceError(`${objKey} doesn't exist on object: ${obj}`);
                        }
                        scope[scopeKey] = f.bind(obj);
                    });
                },
                /**
                 * @global
                 */
                __exitIfError__(action) {
                    try {
                        return action();
                    } catch (err) {
                        if (err instanceof _.Throwable) {
                            runtime.exit(err);
                        } else if (err instanceof Error) {
                            runtime.exit(new _.EvaluatorException(`${err.name}: ${err.message}`, err.fileName, err.lineNumber));
                        } else {
                            runtime.exit();
                        }
                    }
                },
            });

            Object.defineProperties(global, {
                R: {
                    get() {
                        return new _.getProxyObjectInstance(function (type) {
                            return _.cacheR[type] = _.cacheR[type] || new _.getProxyObjectInstance(function (name) {
                                let ctx = typeof activity !== 'undefined' ? activity : context;
                                if (type === 'strings') {
                                    type = 'string';
                                }
                                return ctx.resources.getIdentifier(name, type, ctx.packageName);
                            });
                        });
                    },
                    enumerable: true,
                },
            });
        },
        bindClasses() {
            Object.assign(global, {
                Manifest: android.Manifest,
                Notification: android.app.Notification,
                NotificationManager: android.app.NotificationManager,
                PendingIntent: android.app.PendingIntent,
                Context: android.content.Context,
                Intent: android.content.Intent,
                PackageManager: android.content.pm.PackageManager,
                ColorStateList: android.content.res.ColorStateList,
                Bitmap: android.graphics.Bitmap,
                BitmapFactory: android.graphics.BitmapFactory,
                Paint: android.graphics.Paint,
                ColorDrawable: android.graphics.drawable.ColorDrawable,
                Uri: android.net.Uri,
                Build: android.os.Build,
                Handler: android.os.Handler,
                Looper: android.os.Looper,
                InputType: android.text.InputType,
                TextWatcher: android.text.TextWatcher,
                Linkify: android.text.util.Linkify,
                Base64: android.util.Base64,
                Log: android.util.Log,
                ContextThemeWrapper: android.view.ContextThemeWrapper,
                Gravity: android.view.Gravity,
                KeyEvent: android.view.KeyEvent,
                MotionEvent: android.view.MotionEvent,
                LayoutParams: android.view.WindowManager.LayoutParams,
                MimeTypeMap: android.webkit.MimeTypeMap,
                WebChromeClient: android.webkit.WebChromeClient,
                WebView: android.webkit.WebView,
                WebViewClient: android.webkit.WebViewClient,
                Toast: android.widget.Toast,
                BigTextStyle: androidx.core.app.NotificationCompat.BigTextStyle,
                NotificationCompat: androidx.core.app.NotificationCompat,
                FileProvider: androidx.core.content.FileProvider,
                Snackbar: com.google.android.material.snackbar.Snackbar,
                LogConfigurator: de.mindpipe.android.logging.log4j.LogConfigurator,
                Version: io.github.g00fy2.versioncompare.Version,
                ByteArrayOutputStream: java.io.ByteArrayOutputStream,
                File: java.io.File,
                Runnable: java.lang.Runnable,
                SecurityException: java.lang.SecurityException,
                System: java.lang.System,
                Thread: java.lang.Thread,
                Throwable: java.lang.Throwable,
                URI: java.net.URI,
                StandardCharsets: java.nio.charset.StandardCharsets,
                Locale: java.util.Locale,
                AtomicLong: java.util.concurrent.atomic.AtomicLong,
                ReentrantLock: java.util.concurrent.locks.ReentrantLock,
                Callback: okhttp3.Callback,
                FormBody: okhttp3.FormBody,
                MediaType: okhttp3.MediaType,
                MultipartBody: okhttp3.MultipartBody,
                OkHttpClient: okhttp3.OkHttpClient,
                Request: okhttp3.Request,
                RequestBody: okhttp3.RequestBody,
                Level: org.apache.log4j.Level,
                LogManager: org.apache.log4j.LogManager,
                GlobalAppContext: org.ys.game.app.GlobalAppContext,
                AccessibilityBridge: org.ys.game.core.accessibility.AccessibilityBridge,
                UiSelector: org.ys.game.core.accessibility.UiSelector,
                ApkBuilder: org.ys.game.apkbuilder.ApkBuilder,
                UiObject: org.ys.game.core.automator.UiObject,
                UiObjectCollection: org.ys.game.core.automator.UiObjectCollection,
                Crypto: org.ys.game.core.crypto.Crypto,
                EventEmitter: org.ys.game.core.eventloop.EventEmitter,
                Canvas: org.ys.game.core.graphics.ScriptCanvas,
                MutableOkHttp: org.ys.game.core.http.MutableOkHttp,
                ColorDetector: org.ys.game.core.image.ColorDetector,
                ColorTable: org.ys.game.core.image.ColorTable,
                Image: org.ys.game.core.image.ImageWrapper,
                ImageWrapper: org.ys.game.core.image.ImageWrapper,
                Mat: org.ys.game.core.opencv.Mat,
                DynamicLayoutInflater: org.ys.game.core.ui.inflater.DynamicLayoutInflater,
                JavaScriptEngine: org.ys.game.engine.JavaScriptEngine,
                PFile: org.ys.game.pio.PFile,
                Pref: org.ys.game.pref.Pref,
                ProxyJavaObject: org.ys.game.rhino.ProxyJavaObject,
                ProxyObject: org.ys.game.rhino.ProxyObject,
                ScriptRuntime: org.ys.game.runtime.ScriptRuntime,
                AppUtils: org.ys.game.runtime.api.AppUtils,
                ScreenMetrics: org.ys.game.runtime.api.ScreenMetrics,
                Shell: org.ys.game.runtime.api.Shell,
                ScriptInterruptedException: org.ys.game.runtime.exception.ScriptInterruptedException,
                JavaScriptSource: org.ys.game.script.JavaScriptSource,
                ThemeColor: org.ys.game.theme.ThemeColor,
                App: org.ys.game.util.App,
                ArrayUtils: org.ys.game.util.ArrayUtils,
                ColorUtils: org.ys.game.util.ColorUtils,
                ConsoleUtils: org.ys.game.util.ConsoleUtils,
                DeviceUtils: org.ys.game.util.DeviceUtils,
                DisplayUtils: org.ys.game.util.DisplayUtils,
                JavaUtils: org.ys.game.util.JavaUtils,
                NetworkUtils: org.ys.game.util.NetworkUtils,
                NotificationUtils: org.ys.game.util.NotificationUtils,
                RootMode: org.ys.game.util.RootUtils.RootMode,
                RootUtils: org.ys.game.util.RootUtils,
                StringUtils: org.ys.game.util.StringUtils,
                TextUtils: org.ys.game.util.TextUtils,
                BuildConfig: org.ys.gamecat.BuildConfig,
                VolatileBox: org.autojs.concurrent.VolatileBox,
                Imgproc: org.opencv.imgproc.Imgproc,
            });
        },
        bindModules() {
            // @OrderMatters by SuperMonster003 on May 2, 2022.
            _.bind([
                /* First team */
                [ 'polyfill', 'json', 'util' ],

                /* ! autojs < app */
                /* ! shell < app */
                /* ! files < app */
                /* ! files < crypto */
                [ 'autojs', 'shell', 'files', 'app', 'crypto' ],

                /* ! timers < promise # setTimeout() */
                /* ! timers < automator # setTimeout() in result-adapter */
                /* ! timers < images # setTimeout() in result-adapter */
                [ 'timers', 'promise', 'automator' ],

                /* ! threads < images # threads.atomic() */
                /* ! ui < images */
                /* ! ui < dialogs # ui.run() */
                /* ! colors < dialogs # colors */
                /* ! engines < continuation # engines.myEngine() */
                /* ! continuation < http */
                /* ! ui < http */
                /* ! ui < tasks */
                /* ! threads < tasks */
                /* ! files < tasks */
                /* ! files < ui */
                /* ! files < images */
                [ 'threads', 'ui', 'tasks' ],
                [ 'engines', 'continuation', 'http' ],
                [ 'images', 'colors', 'dialogs' ],

                /* ! http < web */
                [ 'web' ],

                /* ! files < i18n */
                /* ! i18n < selector */
                [ 'i18n', 'selector' ],

                /* ! autojs < RootAutomator */
                [ 'RootAutomator' ],

                /* ! files < console */
                /* ! colors < console */
                /* ! s13n < console */
                [ 's13n', 'console' ],

                /* ! plugins < %extensions% */
                /* ! Arrayx < Mathx */
                /* ! Numberx < Mathx */
                [ 'plugins', 'Arrayx', 'Numberx', 'Mathx' ],

                /* ! images < ocr */
                /* ! images < barcode */
                /* ! images < qrcode */
                /* ! barcode < qrcode */
                /* ! files < ocr */
                [ 'ocr', 'barcode', 'qrcode' ],

                /* Safe to put last regardless of the order, no guarantee ;). */
                [ 'floaty', 'storages', 'device', 'recorder', 'toast', 'media' ],
                [ 'sensors', 'events', 'base64', 'notice', 'shizuku', 'opencc' ],

                /* Last but not the least */
                [ 'globals' ],
            ]);
        },
        bindPrologue() {
            // 重定向 require 以支持相对路径和 npm 模块.
            global.Module = require('jvm-npm');
            global.require = Module.require;

            global.i18n.loadAll();
            global.i18n.setLocale('default');

            // @Comment by SuperMonster003 on Oct 6, 2022.
            //  ! The so-called "protect", has a strong possibility making users annoyed. :(
            //  ! Maybe a better way will come out someday.
            // global.$selfProtect();

            global.$appropriateProtect();

            if (Pref.isCompatibilityWithClassesForVer4xEnabled()) {
                require('redirect').perform();
            }

            if (Pref.isExtendingJsBuildInObjectsEnabled()) {
                plugins.extendAll();
            }
        },
    };

    $.init().bind();

})();