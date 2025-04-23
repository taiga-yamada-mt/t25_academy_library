package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;
    
    @Autowired
    public BookMstService(BookMstRepository bookMstRepository){
        this.bookMstRepository = bookMstRepository;
    }
    
    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }
    public void save(BookMstDto bookMstDto){
        try{
            BookMst book = new BookMst(); //newはクラスを作る　BookMstクラスから

            book.setTitle(bookMstDto.getTitle()); //HTMLの処理をここに
            book.setIsbn(bookMstDto.getIsbn());

            //データベースへの保存
            this.bookMstRepository.save(book);
        } catch(Exception e) {
            throw e;
        }
    }

    //バリデーション処理
    //errTitleListは("""""""")と何個も書ける、Stringは("")一個
    public boolean isValidTitle(String title, Model model) {
        if (StringUtils.isEmpty(title)) {
            model.addAttribute("errTitle", "書籍名を入力してください");
            return true;
        } 
        //書籍名未入力

        if (title.length() > 255){
            model.addAttribute("errTitle", "書籍名は255文字以内で入力してください");
                return true;
        } 
        //書籍名256文字以上
        return false;
            
    }  
    public boolean isValidIsbn(String isbn, Model model) {
        if (StringUtils.isEmpty(isbn)) {
            model.addAttribute("errIsbn", "ISBNを入力してください");
            return true;
        } 
        //ISBN未入力

        if (isbn.length() != 13){
            model.addAttribute("errIsbn", "ISBNは13桁で入力してください");
                

        } 
        //ISBN13桁以外

        if (!isbn.matches("\\d+")) {
            model.addAttribute("errIsbn", "ISBNは半角数字で入力してください");

            
            return true;
        }
        //ISBN半角数字以外
        return false;
        
    }
        //is○○はboolean

    //重複チェック用のメソッド
    public boolean selectByIsbn(String isbn , Model model){
        List<BookMst> book = this.bookMstRepository.selectByIsbn(isbn);

        if(book.size() !=0){
        model.addAttribute("errIsbn","このISBNは既に登録されています");
        return true;
        }
    return false;
    }
}

 






