package com.dreamer.view.data;

import com.dreamer.domain.data.FileData;
import com.dreamer.service.mobile.FileDataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ps.mx.otter.utils.message.Message;

import java.util.Optional;

@RestController
@RequestMapping("/fileData")
public class FileDataController {


    @RequestMapping("/edit.json")
    public Message addFileData(@ModelAttribute("parameter") FileData parameter) {
        try {
            fileDataHandler.merge(parameter);
        } catch (Exception e) {
            e.printStackTrace();
            Message.createFailedMessage(e.getMessage());
        }
        return Message.createSuccessMessage();
    }

    @RequestMapping("/delete.json")
    public Message delete(@ModelAttribute("parameter") FileData data) {
        try {
            fileDataHandler.delete(data);
        } catch (Exception e) {
            e.printStackTrace();
            Message.createFailedMessage(e.getMessage());
        }
        return Message.createSuccessMessage();
    }


    @ModelAttribute("parameter")
    public FileData preprocess(@RequestParam("id") Optional<Integer> id) {
        if (id.isPresent()) {
            return fileDataHandler.get(id.get());
        } else {
            return new FileData();
        }
    }

    @Autowired
    private FileDataHandler fileDataHandler;
}
