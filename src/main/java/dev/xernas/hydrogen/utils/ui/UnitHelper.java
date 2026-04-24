package dev.xernas.hydrogen.utils.ui;

import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.photon.api.window.Window;

import java.util.Locale;
import java.util.function.IntSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitHelper {

    private static Window window;

    public static void init(Window window) {
        UnitHelper.window = window;
    }

    public static IntSupplier getSize(String size) throws HydrogenException {
        if (window == null) throw new HydrogenException("Unit Parser is not initialized yet");
        Pattern pattern = Pattern.compile("^(\\d+)(\\S+)$", Pattern.CASE_INSENSITIVE); // Regex for separating numbers and text
        Matcher matcher = pattern.matcher(size);

        if (!matcher.matches()) throw new HydrogenException("Incorrect size format");

        int number = Integer.parseInt(matcher.group(1));
        String text = matcher.group(2);

        return getSize(number, text);
    }

    public static IntSupplier getSize(int number, String unit) throws HydrogenException {
        return getSize(number, getUnitFromStr(unit));
    }

    public static IntSupplier getSize(IntSupplier number, String unit) throws HydrogenException {
        return getSize(number, getUnitFromStr(unit));
    }

    public static IntSupplier getSize(int number, Unit unit) throws HydrogenException {
        switch (unit) {
            case Unit.PIXEL -> {
                return () -> number;
            }
            case Unit.VIEW_WIDTH -> {
                return () -> {
                    float percent  = number / 100f;
                    return (int) (percent * window.getWidth());
                };
            }
            case Unit.VIEW_HEIGHT -> {
                return () -> {
                    float percent  = number / 100f;
                    return (int) (percent * window.getHeight());
                };
            }
            default -> throw new HydrogenException("Couldn't recognise the unit enum");
        }
    }

    public static IntSupplier getSize(IntSupplier number, Unit unit) throws HydrogenException {
        switch (unit) {
            case Unit.PIXEL -> {
                return number;
            }
            case Unit.VIEW_WIDTH -> {
                return () -> {
                    float percent  = number.getAsInt() / 100f;
                    return (int) (percent * window.getWidth());
                };
            }
            case Unit.VIEW_HEIGHT -> {
                return () -> {
                    float percent  = number.getAsInt() / 100f;
                    return (int) (percent * window.getHeight());
                };
            }
            default -> throw new HydrogenException("Couldn't recognise the unit enum");
        }
    }

    public static IntSupplier add(IntSupplier first, IntSupplier second) {
        return () -> first.getAsInt() - second.getAsInt();
    }

    public static IntSupplier sub(IntSupplier first, IntSupplier second) {
        return () -> first.getAsInt() - second.getAsInt();
    }

    public static IntSupplier scale(IntSupplier size, float scalar) {
        return () -> (int) (size.getAsInt() * scalar);
    }

    public static Unit getUnitFromStr(String unit) throws HydrogenException {
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "px" -> {
                return Unit.PIXEL;
            }
            case "vw" -> {
                return Unit.VIEW_WIDTH;
            }
            case "vh" -> {
                return Unit.VIEW_HEIGHT;
            }
            default -> throw new HydrogenException("Couldn't recognise the unit");
        }
    }

}
