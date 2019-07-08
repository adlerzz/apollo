package org.adlerzz.apollo.bot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeyboardUtils {

    public ReplyKeyboard createInlineKeyboardFromCaptionsList(List<String> captions, String tag){
        List<List<InlineKeyboardButton>> rows = captions.stream()
                .map( caption -> new InlineKeyboardButton(caption).setCallbackData(tag + " " + caption) )
                .map(Collections::singletonList)
                .collect(Collectors.toList());

        return new InlineKeyboardMarkup().setKeyboard(rows);
    }
}
