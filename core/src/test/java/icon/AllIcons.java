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
 *
 */
package icon;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import ui.ComponentDemo;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.components.OverlayScrollPane;
import com.github.weisj.darklaf.icons.IconLoader;
import com.github.weisj.darklaf.icons.ThemedSVGIcon;
import com.github.weisj.darklaf.util.Pair;
import com.kitfox.svg.app.beans.SVGIcon;

public class AllIcons implements ComponentDemo {

    private static final int ICON_SIZE = 50;
    private static final String[] FOLDERS = new String[]{"icons/control", "icons/dialog", "icons/files",
                                                         "icons/indicator", "icons/menu", "icons/misc",
                                                         "icons/navigation", "platform/windows/icons/window",
                                                         "platform/windows/icons"};

    public static void main(final String[] args) {
        ComponentDemo.showDemo(new AllIcons());
    }

    @Override
    public JComponent createComponent() {
        JList<Pair<String, Icon>> list = new JList<>(new ListModel<Pair<String, Icon>>() {
            final List<Pair<String, Icon>> elements = loadIcons();

            @Override
            public int getSize() {
                return elements.size();
            }

            @Override
            public Pair<String, Icon> getElementAt(final int index) {
                return elements.get(index);
            }

            @Override
            public void addListDataListener(final ListDataListener l) {}

            @Override
            public void removeListDataListener(final ListDataListener l) {}
        });
        list.setLayoutOrientation(JList.VERTICAL);
        list.setCellRenderer(new IconListRenderer());
        return new OverlayScrollPane(list);
    }

    private List<Pair<String, Icon>> loadIcons() {
        List<Pair<String, Icon>> list = new ArrayList<>();
        try {
            for (String folder : FOLDERS) {
                Pair<Stream<Path>, Optional<FileSystem>> files = walk(folder, DarkLaf.class);
                try (FileSystem fs = files.getSecond().isPresent() ? files.getSecond().get() : null) {
                    files.getFirst().forEach(p -> {
                        if (p.getFileName().toString().endsWith(".svg")) {
                            int size = ICON_SIZE;
                            ThemedSVGIcon icon = (ThemedSVGIcon) IconLoader.get(DarkLaf.class)
                                                                           .loadSVGIcon(folder + "/" + p.getFileName(),
                                                                                        size, size, true);
                            SVGIcon svgIcon = icon.getSVGIcon();
                            int autosize = svgIcon.getAutosize();
                            svgIcon.setAutosize(SVGIcon.AUTOSIZE_NONE);
                            int width = size;
                            int height = (int) (((double) width / svgIcon.getIconWidth()) * svgIcon.getIconHeight());
                            if (height > size) {
                                height = size;
                                width = (int) (((double) height / svgIcon.getIconHeight()) * svgIcon.getIconWidth());
                            }

                            icon.setDisplaySize(width, height);
                            svgIcon.setAutosize(autosize);
                            list.add(new Pair<>(p.getFileName().toString(), new CenterIcon(icon, size, size)));
                        }
                    });
                }
            }
        } catch (final IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Pair<Stream<Path>, Optional<FileSystem>> walk(final String path,
                                                         final Class<?> clazz) throws URISyntaxException, IOException {
        URI uri = clazz.getResource(path).toURI();
        if ("jar".equals(uri.getScheme())) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            Path resourcePath = fileSystem.getPath("com/github/weisj/darklaf/" + path);
            // Get all contents of a resource (skip resource itself), if entry is a directory remove trailing /
            return new Pair<>(Files.walk(resourcePath, 1), Optional.of(fileSystem));
        } else {
            return new Pair<>(Arrays.stream(Optional.ofNullable(new File(uri).listFiles())
                                                    .orElse(new File[0]))
                                    .map(File::toPath),
                              Optional.empty());
        }
    }

    @Override
    public String getTitle() {
        return "All Icons";
    }

    private static final class IconListRenderer extends JLabel implements ListCellRenderer<Pair<String, Icon>> {

        private IconListRenderer() {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }

        @Override
        public Component getListCellRendererComponent(final JList<? extends Pair<String, Icon>> list,
                                                      final Pair<String, Icon> value, final int index,
                                                      final boolean isSelected, final boolean cellHasFocus) {
            setIcon(value.getSecond());
            setText(value.getFirst());
            return this;
        }
    }

    private static class CenterIcon implements Icon {

        private final Icon icon;
        private final int width;
        private final int height;

        private CenterIcon(final Icon icon, final int width, final int height) {
            this.icon = icon;
            this.width = width;
            this.height = height;
        }

        @Override
        public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
            int px = x + (width - icon.getIconWidth()) / 2;
            int py = y + (height - icon.getIconHeight()) / 2;
            icon.paintIcon(c, g, px, py);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}
