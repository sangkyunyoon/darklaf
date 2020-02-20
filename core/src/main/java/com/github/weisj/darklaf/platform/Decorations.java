/*
 * MIT License
 *
 * Copyright (c) 2020 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.weisj.darklaf.platform;

import com.github.weisj.darklaf.platform.windows.WindowsDecorationsProvider;
import com.github.weisj.darklaf.util.SystemInfo;
import com.github.weisj.decorations.CustomTitlePane;
import com.github.weisj.decorations.DecorationsProvider;

import javax.swing.*;
import java.util.Properties;

public final class Decorations {

    private static DecorationsProvider decorationsProvider;

    static {
        //Extend for different platforms.
        if (SystemInfo.isWindows) {
            decorationsProvider = new WindowsDecorationsProvider();
        } else {
            decorationsProvider = new DefaultDecorationsProvider();
        }
    }

    public static CustomTitlePane createTitlePane(final JRootPane rootPane) {
        return decorationsProvider.createTitlePane(rootPane);
    }


    public static boolean isCustomDecorationSupported() {
        return decorationsProvider.isCustomDecorationSupported();
    }

    public static void initialize() {
        decorationsProvider.initialize();
    }

    public static void loadDecorationProperties(final Properties uiProps, final UIDefaults defaults) {
        decorationsProvider.loadDecorationProperties(uiProps, defaults);
    }
}